package solver

import domain.Album
import domain.Slide
import org.optaplanner.core.api.score.buildin.simple.SimpleScore
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator

class ScoreCalc: IncrementalScoreCalculator<Album, SimpleScore> {
    private var score: Int = 0

    override fun resetWorkingSolution(workingSolution: Album) {
        this.score = 0

        for (slide in workingSolution.slides) {
            appendSlide(slide)
        }
//        println("Finished resetting")
    }

    override fun beforeEntityAdded(entity: Any?) {
        // Do nothing
    }

    override fun afterEntityAdded(entity: Any?) {
        appendSlide(entity as Slide)
    }

    override fun beforeEntityRemoved(entity: Any?) {
        removeSlot(entity as Slide)
    }

    override fun afterEntityRemoved(entity: Any?) {
        // Do nothing
    }

    override fun beforeVariableChanged(entity: Any?, variableName: String?) {
        removeSlot(entity as Slide)
    }

    override fun afterVariableChanged(entity: Any?, variableName: String?) {
        appendSlide(entity as Slide)
    }

    override fun calculateScore(): SimpleScore {
        return SimpleScore.of(score)
    }

    private fun removeSlot(slide: Slide) {
//        println("Going to remove ${slide}, score is ${this.score}, slide score is ${slide.score()}")
        this.score -= slide.score()
//        println("Removed ${slide}, score is ${this.score}")
    }

    private fun appendSlide(slide: Slide) {
//        println("Going to append ${slide}, score is ${this.score}, slide score is ${slide.score()}")
        this.score += slide.score()
//        println("Added ${slide}, score is ${this.score}")
    }
}