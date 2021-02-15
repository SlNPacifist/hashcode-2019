package domain

import kotlin.math.min

abstract class Slide {
    abstract var id: Int?
    abstract var tags: Array<Int>?
    abstract var next: Slide?
    abstract var prev: Slide?
    abstract fun photosIterator(): Sequence<Photo>

    fun score(): Int {
        val nextSlideTags = this.next?.tags ?: return 0
        val thisSlideTags = this.tags ?: return 0

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
}