package solver

import domain.Album
import domain.Slide
import domain.VerticalSlide
import org.optaplanner.core.impl.score.director.ScoreDirector
import org.optaplanner.core.impl.heuristic.move.AbstractMove

class SwapVerticalPhotosMove(val a: VerticalSlide, val b: VerticalSlide, val pickSecond: Boolean) : AbstractMove<Album>() {
    override fun doMoveOnGenuineVariables(scoreDirector: ScoreDirector<Album>) {
        val changedSlides = setOf(a, a.prev, b, b.prev)
        this.beforeChanged(changedSlides, scoreDirector)

        val aPhoto = a.photo1
        if (pickSecond) {
            a.photo1 = b.photo2
            b.photo2 = aPhoto
        } else {
            a.photo1 = b.photo1
            b.photo1 = aPhoto
        }

        a.recalculateTags()
        b.recalculateTags()

        this.afterChanged(changedSlides, scoreDirector)
    }

    fun beforeChanged(slots: Set<Slide?>, scoreDirector: ScoreDirector<Album>) {
        for (slot in slots) {
            if (slot != null) {
                scoreDirector.beforeVariableChanged(slot, "next")
            }
        }
    }

    fun afterChanged(slots: Set<Slide?>, scoreDirector: ScoreDirector<Album>) {
        for (slot in slots) {
            if (slot != null) {
                scoreDirector.afterVariableChanged(slot, "next")
            }
        }
    }

    override fun isMoveDoable(scoreDirector: ScoreDirector<Album>): Boolean {
        return true
    }

    override fun createUndoMove(scoreDirector: ScoreDirector<Album>): SwapVerticalPhotosMove {
        return SwapVerticalPhotosMove(a, b, pickSecond)
    }

    override fun toString(): String {
        return "Swap first photo of $a and ${if (pickSecond) "second" else "first"} photo of $b"
    }

    // Required for multithreading support
    override fun rebase(destinationScoreDirector: ScoreDirector<Album>): SwapVerticalPhotosMove {
        return SwapVerticalPhotosMove(
            destinationScoreDirector.lookUpWorkingObject(a),
            destinationScoreDirector.lookUpWorkingObject(b),
            pickSecond
        )
    }
}