package domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.simple.SimpleScore
import java.io.File

//@PlanningSolution(solutionCloner = AlbumCloner::class)
@PlanningSolution
class Album {
    @ProblemFactCollectionProperty
    lateinit var horizontalPhotos: Array<Photo>

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "verticalPhotos")
    lateinit var verticalPhotos: Array<Photo>

    // TODO: do we need this?
    lateinit var horizontalSlides: Array<Slide>
    lateinit var verticalSlides: Array<Slide>

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "slides")
    lateinit var slides: Array<Slide>

    @PlanningScore
    var score: SimpleScore? = null

    constructor() {}

    constructor(horizontalPhotos: Array<Photo>, verticalPhotos: Array<Photo>) {
        this.horizontalPhotos = horizontalPhotos
        this.verticalPhotos = verticalPhotos
        this.horizontalSlides = horizontalPhotos.mapIndexed() { i, p -> HorizontalSlide(i, p) }.toTypedArray()
        this.verticalSlides = verticalPhotos.toList().chunked(2).mapIndexed { i, p -> VerticalSlide(i, p[0], p[1]) }.toTypedArray()
        this.slides = arrayOf(*this.horizontalSlides, *this.verticalSlides)

        for (i in 0..this.slides.size - 2) {
            this.slides[i].next = this.slides[i + 1]
            this.slides[i + 1].prev = this.slides[i]
        }
        this.slides.last().next = EndSlide()
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