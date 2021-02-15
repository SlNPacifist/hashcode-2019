package domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity
class VerticalSlide : Slide {
    @PlanningId
    override var id: Int? = null

    override var tags: Array<Int>? = null

    @PlanningVariable(valueRangeProviderRefs = ["slides"])
    override var next: Slide? = null
    override var prev: Slide? = null

    @PlanningVariable(valueRangeProviderRefs = ["verticalPhotos"])
    lateinit var photo1: Photo

    @PlanningVariable(valueRangeProviderRefs = ["verticalPhotos"])
    lateinit var photo2: Photo

    constructor() : super() {}

    constructor(id: Int, photo1: Photo, photo2: Photo) : super() {
        this.id = id
        this.photo1 = photo1
        this.photo2 = photo2
        this.recalculateTags()
    }

    fun recalculateTags() {
        this.tags = arrayOf(*photo1.tags, *photo2.tags).distinct().toTypedArray()
    }

    override fun photosIterator(): Sequence<Photo> {
        val photo1 = this.photo1
        val photo2 = this.photo2
        return sequence {
            yield(photo1)
            yield(photo2)
        }
    }

    override fun toString(): String {
        return "V ${this.photo1} ${this.photo2}"
    }
}