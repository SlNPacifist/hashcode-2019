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
        val nextSlide = this.next?.slide ?: return 0
        val thisSlide = this.slide ?: return 0
        val common = thisSlide.tags.intersect(nextSlide.tags).size
        val uniqueThis = thisSlide.tags.filter { !nextSlide.tags.contains(it) }.size
        val uniqueNext = nextSlide.tags.filter { !thisSlide.tags.contains(it) }.size
        return min(common, min(uniqueNext, uniqueThis))
    }

    override fun toString(): String {
        return "Slot $id"
    }
}
