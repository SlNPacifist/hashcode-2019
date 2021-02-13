import org.optaplanner.core.impl.score.director.ScoreDirector
import org.optaplanner.core.impl.heuristic.move.AbstractMove

class PlaceSlideMove(val b: SlotInterface, val d: SlotInterface?) : AbstractMove<Album>() {
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

            b.next = head
            head.prev = b
            a?.next = c
            c?.prev = a
            b.prev = null

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

    fun beforeChange(scoreDirector: ScoreDirector<Album>, slot: SlotInterface?) {
        if (slot != null) {
            scoreDirector.beforeVariableChanged(slot, "next")
        }
    }

    fun afterChange(scoreDirector: ScoreDirector<Album>, slot: SlotInterface?) {
        if (slot != null) {
            scoreDirector.afterVariableChanged(slot, "next")
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
}