package domain

class EndSlot: SlotInterface() {
    override var id: Int? = null
    override var slide: Slide? = null
    override var next: SlotInterface? = null
    override var prev: SlotInterface? = null

    override fun toString(): String {
        return "End slot"
    }
}
