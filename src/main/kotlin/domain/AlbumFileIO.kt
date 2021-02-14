package domain
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO
import java.io.File

// SolutionReader is needed for benchmark
class AlbumFileIO : SolutionFileIO<Album> {
    override fun getInputFileExtension(): String {
        return "txt"
    }

    override fun read(file: File?): Album {
        return Album.fromFile(file ?: throw error("No input file"))
    }

    override fun write(album: Album?, file: File?) {
        file?.writeText(album.toString())
    }
}