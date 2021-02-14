package solver

import domain.Album
import org.optaplanner.core.impl.heuristic.move.Move
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory
import org.optaplanner.core.impl.score.director.ScoreDirector
import java.util.*

class PlaceSlideMoveFactory : MoveIteratorFactory<Album> {
    override fun getSize(scoreDirector: ScoreDirector<Album>): Long {
        val totalSlots = scoreDirector.workingSolution.slots.size.toLong()
        return totalSlots * totalSlots
    }

    override fun createOriginalMoveIterator(scoreDirector: ScoreDirector<Album>): Iterator<Move<Album>> {
        val slots = scoreDirector.workingSolution.slots
        return sequence {
//            println("createOriginalMoveIterator start")
//            println("slots: ${ slots.slice(0..10).map{ "${it.prev?.id} => ${it.id} => ${it.next?.id} " } }")
            for (b in slots) {
                if (b.prev != null) {
//                    println("createOriginalMoveIterator ${PlaceSlideMove(b, null)}")
                    yield(PlaceSlideMove(b, null))
                }
                for (d in slots) {
                    if (d != b.prev && d != b) {
//                        println("createOriginalMoveIterator ${PlaceSlideMove(b, d)}")
                        yield(PlaceSlideMove(b, d))
                    }
                }
            }
        }.iterator()
    }

    override fun createRandomMoveIterator(scoreDirector: ScoreDirector<Album>, workingRandom: Random): Iterator<Move<Album>> {
        val slots = scoreDirector.workingSolution.slots
        return sequence {
//            println("createRandomMoveIterator start")
//            println("slots: ${ slots.slice(0..10).map{ "${it.prev?.id} => ${it.id} => ${it.next?.id} " } }")

            while (true) {
                val bIdx = workingRandom.nextInt(slots.size)
                val dIdx = workingRandom.nextInt(slots.size)

                val b = slots[bIdx]
                val d = slots[dIdx]

                if (d == b.prev) {
                    // Same position - move b after d, when d is b.prev
                    continue
                }
                else if (b == d) {
                    // If b is head, same d will never be p.prev, but one case needs to be excluded
                    if (b.prev == null) {
                        // b is head, so skip this
                        continue
                    }

                    // Special case - instead of moving after self move to begining
//                    println("createRandomMoveIterator ${PlaceSlideMove(b, null)}")
                    yield(PlaceSlideMove(b, null))
                }
                else {
//                    println("createRandomMoveIterator ${PlaceSlideMove(b, d)}")
                    yield(PlaceSlideMove(b, d))
                }
            }
        }.iterator()
    }
}