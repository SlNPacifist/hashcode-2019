package domain

import org.optaplanner.core.api.domain.lookup.PlanningId

class EndSlide: Slide() {
    @PlanningId
    override var id: Int? = 0

    override var tags: Array<Int>? = null
    override var next: Slide? = null
    override var prev: Slide? = null

    override fun photosIterator(): Sequence<Photo> {
        return sequence<Photo> {}
    }

    override fun toString(): String {
        return "E"
    }
}
