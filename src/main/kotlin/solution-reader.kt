//import domain.Coloring
//import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO
//import java.io.File
//
//class SolutionReader : SolutionFileIO<Coloring> {
//    override fun getInputFileExtension(): String {
//        return ""
//    }
//
//    override fun read(file: File?): Coloring {
//        return Coloring.fromFile(file ?: throw error("No input file"))
//    }
//
//    override fun write(coloring: Coloring?, file: File?) {
//        file?.writeText(coloring.toString())
//    }
//}