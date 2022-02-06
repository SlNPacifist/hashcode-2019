package domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable

fun joinTags(a: Array<Int>, b: Array<Int>): Array<Int> {
    return arrayOf(*a, *b).distinct().toTypedArray()
}

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
        this.tags = joinTags(photo1.tags, photo2.tags)
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