package domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.simple.SimpleScore
import java.io.File

fun combinePhotosByTag(photos: Array<Photo>): HashMap<Int, MutableList<Photo>> {
    val res = hashMapOf<Int, MutableList<Photo>>();
    for (photo in photos) {
        for (tag in photo.tags) {
            res.getOrPut(tag, { mutableListOf() } ).add(photo)
        }
    }
    return res
}

//@PlanningSolution(solutionCloner = AlbumCloner::class)
@PlanningSolution
class Album {
    @ProblemFactCollectionProperty
    lateinit var horizontalPhotos: Array<Photo>

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "verticalPhotos")
    lateinit var verticalPhotos: Array<Photo>

    // TODO: do we need horizontalSlides?
    lateinit var horizontalSlides: Array<HorizontalSlide>
    lateinit var verticalSlides: Array<VerticalSlide>

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "slides")
    lateinit var slides: Array<Slide>

    lateinit var horizontalPhotosByTag: HashMap<Int, MutableList<Photo>>;
    lateinit var verticalPhotosByTag: HashMap<Int, MutableList<Photo>>;

    @PlanningScore
    var score: SimpleScore? = null

    constructor() {}

    constructor(horizontalPhotos: Array<Photo>, verticalPhotos: Array<Photo>) {
        this.horizontalPhotos = horizontalPhotos
        this.verticalPhotos = verticalPhotos
        this.horizontalPhotosByTag = combinePhotosByTag(horizontalPhotos)
        this.verticalPhotosByTag = combinePhotosByTag(verticalPhotos)

        val end = EndSlide()

        val greedy = true
        if (greedy) {
            val horSlides = mutableListOf<HorizontalSlide>()
            val vertSlides = mutableListOf<VerticalSlide>()
            val usedPhotos = hashSetOf<Photo>()
            var totalScore = 0
            val slidesNeeded = this.horizontalPhotos.size + this.verticalPhotos.size / 2
            fun useSlide(slide: Slide): Slide {
                for (photo in slide.photosIterator()) {
                    usedPhotos.add(photo)
                }
                if (slide is HorizontalSlide) {
                    horSlides.add(slide)
                } else if (slide is VerticalSlide) {
                    vertSlides.add(slide)
                }
                return slide
            }
            var lastSlide = useSlide(this.createBestSlideFor(0, arrayOf(), usedPhotos))
            for (i in 1 until slidesNeeded) {
                val bestSlide = useSlide(this.createBestSlideFor(i, lastSlide.tags ?: arrayOf(), usedPhotos))
                lastSlide.next = bestSlide
                bestSlide.prev = lastSlide
                val addedScore = lastSlide.score()
                totalScore += addedScore
                lastSlide = bestSlide
                println("Added score: $addedScore")
                println("Total score: $totalScore")
                println("Slides left: ${slidesNeeded - i - 1}")
            }
            lastSlide.next = end
            end.prev = lastSlide
            this.horizontalSlides = horSlides.toTypedArray()
            this.verticalSlides = vertSlides.toTypedArray()
        }
        else {
            this.horizontalSlides = horizontalPhotos.mapIndexed() { i, p -> HorizontalSlide(i, p) }.toTypedArray()
            this.verticalSlides = verticalPhotos.toList().chunked(2).mapIndexed { i, p -> VerticalSlide(i, p[0], p[1]) }.toTypedArray()
            for (i in 0..this.slides.size - 2) {
                this.slides[i].next = this.slides[i + 1]
                this.slides[i + 1].prev = this.slides[i]
            }
            val lastSlide = this.slides.last()
            lastSlide.next = end
            end.prev = lastSlide
        }
        this.slides = arrayOf(*this.horizontalSlides, *this.verticalSlides)
    }

    fun createBestSlideFor(id: Int, tags: Array<Int>, usedPhotos: HashSet<Photo>): Slide {
        val bestHorizontalSlide = this.createBestHorizontalSlideFor(id, tags, usedPhotos)
        val horScore = score(tags, bestHorizontalSlide?.tags ?: arrayOf())
        val bestVerticalSlide = this.createBestVerticalSlideFor(id, tags, usedPhotos)
        val vertScore = score(tags, bestVerticalSlide?.tags ?: arrayOf())

        return if (bestHorizontalSlide == null || bestVerticalSlide == null) {
            bestHorizontalSlide ?: bestVerticalSlide ?: throw Error("Somehow got null when creating best slide")
        } else {
            if (horScore >= vertScore) {
                bestHorizontalSlide
            } else {
                bestVerticalSlide
            }
        }
    }

    fun createBestHorizontalSlideFor(id: Int, tags: Array<Int>, usedPhotos: HashSet<Photo>): HorizontalSlide? {
        return (
            tags.flatMap { this.horizontalPhotosByTag.getOrDefault(it, mutableListOf()) }
                .filter { !usedPhotos.contains(it) }
                .take(100)
                .maxByOrNull { score(tags, it.tags) }
            ?: this.horizontalPhotos
                .firstOrNull { !usedPhotos.contains(it) }
        )?.let { HorizontalSlide(id, it) }
    }

    fun createBestVerticalSlideFor(id: Int, tags: Array<Int>, usedPhotos: HashSet<Photo>): VerticalSlide? {
        fun firstNotUsed(except: Photo?): Photo? {
            return this.verticalPhotos.firstOrNull { !usedPhotos.contains(it) && it != except }
        }

        val bestSlide = tags.flatMap { this.verticalPhotosByTag.getOrDefault(it, mutableListOf()) }
            .filter { !usedPhotos.contains(it) }
            .maxByOrNull { score(tags, it.tags) }
            ?.let { first ->
                var bestVerticalPair = tags.filter { !first.tags.contains(it) }
                    .flatMap { this.verticalPhotosByTag.getOrDefault(it, mutableListOf()) }
                    .filter { !usedPhotos.contains(it) && it != first }
                    .maxByOrNull { score(tags, joinTags(first.tags, it.tags)) }
                bestVerticalPair = bestVerticalPair ?: firstNotUsed(first) ?: return null
                VerticalSlide(id, first, bestVerticalPair)
            }

        return if (bestSlide == null) {
            val first = firstNotUsed(null) ?: return null
            VerticalSlide(id, first, firstNotUsed(first) ?: return null)
        } else {
            bestSlide
        }
    }

    companion object {
        fun fromFile(file: File): Album {
            val tagDict = HashMap<String, Int>()
            val photos = file.bufferedReader().useLines {
                val iter = it.iterator()
                val photosCount = iter.next().toInt()
                iter.asSequence().take(photosCount).mapIndexed { id, x -> Photo.fromString(id, x, tagDict) }.toList()
            }

            val (vertical, horizontal) = photos.partition { it.isVertical }

            return Album(horizontal.toTypedArray(), vertical.toTypedArray())
        }

        fun fromFileName(filename: String): Album {
            return fromFile(File(filename))
        }
    }

    override fun toString(): String {
//        return this.slots.map { "${it?.prev?.id}->${it.id}->${it?.next?.id}" }.joinToString("\n")
        var head: Slide = this.slides[0]
        while (true) {
            head = head.prev ?: break
        }

        val parts = mutableListOf("${this.slides.size}")
        var cur: Slide? = head
        while (cur != null) {
            parts.add(cur.photosIterator().map { it.id }.joinToString(" "))
            cur = cur.next
        }
        return parts.joinToString("\n")
    }
}

//class AlbumCloner : SolutionCloner<Album> {
//    override fun cloneSolution(original: Album): Album {
//        val clone = Album()
//        clone.slides = original.slides
//        clone.score = original.score
//        val newSlots = Array(original.slots.size) { Slot(original.slots[it].id!!, original.slots[it].slide!!) }
//
//        for (slot in original.slots) {
//            val newSlot = newSlots[slot.id!!]
//            if (slot.next != null) {
//                if (slot.next is EndSlot) {
//                    newSlot.next = slot.next
//                } else {
//                    newSlot.next = newSlots[slot.next!!.id!!]
//                }
//            }
//            if (slot.prev != null) {
//                newSlot.prev = newSlots[slot.prev!!.id!!]
//            }
//        }
//
//        clone.slots = newSlots
//
//        return clone
//    }
//}