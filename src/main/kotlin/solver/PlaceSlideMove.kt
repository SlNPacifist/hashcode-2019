package solver

import domain.Album
import domain.Slide
import org.optaplanner.core.impl.score.director.ScoreDirector
import org.optaplanner.core.impl.heuristic.move.AbstractMove
import org.optaplanner.core.impl.heuristic.move.Move
import java.lang.Error

class PlaceSlideMove(val b: Slide, val d: Slide?) : AbstractMove<Album>() {
    override fun doMoveOnGenuineVariables(scoreDirector: ScoreDirector<Album>) {
//        println(this)
//        println("Solution before move:")
//        println(scoreDirector.workingSolution)
        if (d == null) {
            // insert into head
            var head = b
            while (true) {
                head = head.prev ?: break
            }

            val c = b.next
            val a = b.prev

            this.beforeChange(scoreDirector, b)
            this.beforeChange(scoreDirector, a)

            // head -> ... -> a -> b -> c -> ...
            // head == a -> b -> c -> ...

            b.next = head
            head.prev = b
            a?.next = c
            c?.prev = a
            b.prev = null

            // b -> head -> ... -> a -> c -> ...
            // b -> head == a -> c -> ...

            this.afterChange(scoreDirector, b)
            this.afterChange(scoreDirector, a)
        } else {
            // a -> b -> c .. d -> e
            val a = b.prev
            val c = b.next
            val e = d.next

            this.beforeChange(scoreDirector, d)
            this.beforeChange(scoreDirector, b)
            this.beforeChange(scoreDirector, a)

            d.next = b
            b.prev = d
            b.next = e
            e?.prev = b
            a?.next = c
            c?.prev = a

            // a -> c .. d -> b -> e

            this.afterChange(scoreDirector, d)
            this.afterChange(scoreDirector, b)
            this.afterChange(scoreDirector, a)
        }
    }

    fun beforeChange(scoreDirector: ScoreDirector<Album>, slide: Slide?) {
        if (slide != null) {
            scoreDirector.beforeVariableChanged(slide, "next")
        }
    }

    fun afterChange(scoreDirector: ScoreDirector<Album>, slide: Slide?) {
        if (slide != null) {
            scoreDirector.afterVariableChanged(slide, "next")
        }
    }

    override fun isMoveDoable(scoreDirector: ScoreDirector<Album>?): Boolean {
        return true
    }

    override fun createUndoMove(scoreDirector: ScoreDirector<Album>?): AbstractMove<Album> {
        return PlaceSlideMove(b, b.prev)
    }

    override fun toString(): String {
        return "Place $b after $d"
    }

    // Required for multithreading support
    override fun rebase(destinationScoreDirector: ScoreDirector<Album>?): Move<Album> {
        if (destinationScoreDirector == null) {
            throw Error("rebase called without destinationScoreDirector")
        }
        val newD = if (d == null) null else destinationScoreDirector.lookUpWorkingObject(d)
        return PlaceSlideMove(destinationScoreDirector.lookUpWorkingObject(b), newD)
    }
}