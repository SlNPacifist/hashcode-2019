abstract class SlotInterface {
    abstract var id: Int?
    abstract var slide: Slide?
    abstract var next: SlotInterface?
    abstract var prev: SlotInterface?
}