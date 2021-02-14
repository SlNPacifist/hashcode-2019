package solver

import domain.Album
import domain.Slot
import org.optaplanner.core.api.score.buildin.simple.SimpleScore
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator

class ScoreCalc: IncrementalScoreCalculator<Album, SimpleScore> {
    private var score: Int = 0

    override fun resetWorkingSolution(workingSolution: Album?) {
        this.score = 0
        if (workingSolution == null) {
            return
        }

        for (slot in workingSolution.slots) {
            appendSlot(slot)
        }
//        println("Finished resetting")
    }

    override fun beforeEntityAdded(entity: Any?) {
        // Do nothing
    }

    override fun afterEntityAdded(entity: Any?) {
        appendSlot(entity as Slot)
    }

    override fun beforeEntityRemoved(entity: Any?) {
        removeSlot(entity as Slot)
    }

    override fun afterEntityRemoved(entity: Any?) {
        // Do nothing
    }

    override fun beforeVariableChanged(entity: Any?, variableName: String?) {
        removeSlot(entity as Slot)
    }

    override fun afterVariableChanged(entity: Any?, variableName: String?) {
        appendSlot(entity as Slot)
    }

    override fun calculateScore(): SimpleScore {
        return SimpleScore.of(score)
    }

    private fun removeSlot(slot: Slot) {
//        println("Going to remove ${slot}, score is ${this.score}, slot score is ${slot.score()}")
        this.score -= slot.score()
//        println("Removed ${slot}, score is ${this.score}")
    }

    private fun appendSlot(slot: Slot) {
//        println("Going to append ${slot}, score is ${this.score}, slot score is ${slot.score()}")
        this.score += slot.score()
//        println("Added ${slot}, score is ${this.score}")
    }
}