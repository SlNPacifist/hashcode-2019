package domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity
class HorizontalSlide : Slide {
    @PlanningId
    override var id: Int? = null

    override var tags: Array<Int>? = null
    get() = this.photo.tags

    @PlanningVariable(valueRangeProviderRefs = ["slides"])
    override var next: Slide? = null
    override var prev: Slide? = null

    lateinit var photo: Photo

    constructor() : super() {}

    constructor(id: Int, photo: Photo) : super() {
        this.id = id
        this.photo = photo
    }

    override fun photosIterator(): Sequence<Photo> {
        val photo = this.photo
        return sequence {
            yield(photo)
        }
    }

    override fun toString(): String {
        return "H ${this.photo}"
    }
}