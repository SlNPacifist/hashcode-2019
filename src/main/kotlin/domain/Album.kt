package domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.simple.SimpleScore
import java.io.File

@PlanningSolution
class Album {
    lateinit var slides: Array<Slide>

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "slots")
    lateinit var slots: Array<Slot>

    @PlanningScore
    var score: SimpleScore? = null

    constructor() {}

    constructor(slides: Array<Slide>) {
        this.slides = slides
        this.slots = Array(slides.size) { Slot(it, this.slides[it]) }
        for (i in 0..this.slots.size - 2) {
            this.slots[i].next = this.slots[i + 1]
            this.slots[i + 1].prev = this.slots[i]
        }
        this.slots.last().next = EndSlot()
    }

    companion object {
        fun fromFile(file: File): Album {
            val photos = file.bufferedReader().useLines {
                val it = it.iterator()
                val photosCount = it.next().toInt()
                it.asSequence().take(photosCount).mapIndexed { id, x -> Photo.fromString(id, x) }.toList()
            }

            var lastVerticalPhoto: Photo? = null
            val slides = mutableListOf<Slide>()
            for (photo in photos) {
                if (photo.isVertical) {
                    if (lastVerticalPhoto != null) {
                        slides.add(Slide(listOf(lastVerticalPhoto, photo)))
                        lastVerticalPhoto = null
                    } else {
                        lastVerticalPhoto = photo
                    }
                } else {
                    slides.add(Slide(listOf(photo)))
                }
            }

            return Album(slides.toTypedArray())
        }

        fun fromFileName(filename: String): Album {
            return fromFile(File(filename))
        }
    }

    override fun toString(): String {
//        return this.slots.map { "${it?.prev?.id}->${it.id}->${it?.next?.id}" }.joinToString("\n")
        var head: SlotInterface = this.slots[0]
        while (true) {
            head = head.prev ?: break
        }

        val parts = mutableListOf<String>("${this.slots.size}")
        var cur: SlotInterface? = head
        while (cur != null) {
            val part = cur.slide?.photos?.map { it.id }?.joinToString(" ")
            if (part != null) {
                parts.add(part)
            }
            cur = cur.next
        }
        return parts.joinToString("\n")
    }
}