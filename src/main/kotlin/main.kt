import domain.Album
import org.optaplanner.core.api.solver.SolverFactory


fun main(args: Array<String>) {
    // Path relative to src/main/kotlin/resources
    val solver = SolverFactory.createFromXmlResource<Album>("BaseSolverConfig.xml").buildSolver()

    var iter = 0
    val mult = 1
    solver.addEventListener {
        val solution = it.newBestSolution
        val score = solution.score
        if (score !== null && score.isFeasible) {
            if (iter % mult == 0) println("Score: $score")
//            println(solution.toString())
        }

        iter += 1
    }

    // Path relative to cwd
    val album = Album.fromFileName("data/c_memorable_moments.txt")
    solver.solve(album)
}