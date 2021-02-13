import org.optaplanner.core.api.solver.SolverFactory


fun main(args: Array<String>) {
    val solver = SolverFactory.createFromXmlResource<Album>("BaseSolverConfig.xml").buildSolver()
    solver.addEventListener {
        val solution = it.newBestSolution
        val score = solution.score
        if (score !== null && score.isFeasible) {
            println("Score: $score")
            println(solution.toString())
        }
    }
    val album = Album.fromFileName("data/c_memorable_moments.txt")
    solver.solve(album)
}