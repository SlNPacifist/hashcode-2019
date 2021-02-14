package domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable
import kotlin.math.min

@PlanningEntity
class Slot : SlotInterface {
    @PlanningId
    override var id: Int? = null

    override var slide: Slide? = null

    @PlanningVariable(valueRangeProviderRefs = ["slots"])
    override var next: SlotInterface? = null
    override var prev: SlotInterface? = null

    constructor() {}

    constructor(id: Int, slide: Slide) {
        this.id = id
        this.slide = slide
    }

    fun score(): Int {
        val nextSlideTags = this.next?.slide?.tags ?: return 0
        val thisSlideTags = this.slide?.tags ?: return 0

        var common = 0
        var uniqueThis = 0
        var uniqueNext = 0

        ttLoop@ for (tt in thisSlideTags) {
            for (nt in nextSlideTags) {
                if (tt == nt) {
                    common += 1;
                    continue@ttLoop
                }
            }
            uniqueThis += 1
        }

        ntLoop@ for (nt in nextSlideTags) {
            for (tt in thisSlideTags) {
                if (tt == nt) {
                    continue@ntLoop
                }
            }
            uniqueNext += 1
        }

        return min(common, min(uniqueNext, uniqueThis))
    }

    override fun toString(): String {
        return "domain.Slot $id"
    }
}
