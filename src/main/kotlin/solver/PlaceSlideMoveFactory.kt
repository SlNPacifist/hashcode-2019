package solver

import domain.Album
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory
import java.util.ArrayList


class PlaceSlideMoveFactory : MoveListFactory<Album> {
    override fun createMoveList(album: Album): List<PlaceSlideMove> {
        val moveList: MutableList<PlaceSlideMove> = ArrayList<PlaceSlideMove>()
        for (b in album.slots) {
            if (b.prev != null) {
                moveList.add(PlaceSlideMove(b, null))
            }
            for (d in album.slots) {
                if (d != b.prev && d != b) {
                    moveList.add(PlaceSlideMove(b, d))
                }
            }
        }
        return moveList
    }
}