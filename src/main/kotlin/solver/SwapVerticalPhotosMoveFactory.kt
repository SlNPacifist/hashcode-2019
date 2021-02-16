package solver

import domain.Album
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory
import org.optaplanner.core.impl.score.director.ScoreDirector
import java.util.*

class SwapVerticalPhotosMoveFactory : MoveIteratorFactory<Album> {
    override fun getSize(scoreDirector: ScoreDirector<Album>): Long {
        val slidesCount = scoreDirector.workingSolution.verticalSlides.size.toLong()
        // slidesCount ^ 2 / 2 total unique pairs, 4 combinations in each
        return slidesCount * slidesCount * 2
    }

    override fun createOriginalMoveIterator(scoreDirector: ScoreDirector<Album>): Iterator<SwapVerticalPhotosMove> {
        val slides = scoreDirector.workingSolution.verticalSlides
        return sequence {
            for (i in slides.indices) {
                val a = slides[i]
                for (j in slides.indices) {
                    if (i == j) {
                        continue
                    }
                    val b = slides[j]
                    yield(SwapVerticalPhotosMove(a, b, false))
                    yield(SwapVerticalPhotosMove(a, b, true))
                }
            }
        }.iterator()
    }

    override fun createRandomMoveIterator(
        scoreDirector: ScoreDirector<Album>,
        workingRandom: Random
    ): Iterator<SwapVerticalPhotosMove> {
        val slots = scoreDirector.workingSolution.verticalSlides
        return sequence {
            while (true) {
                val i = workingRandom.nextInt(slots.size)
                val j = workingRandom.nextInt(slots.size)
                if (i == j) {
                    continue
                }
                val a = slots[i]
                val b = slots[j]

                yield(SwapVerticalPhotosMove(a, b, workingRandom.nextBoolean()))
            }
        }.iterator()
    }
}