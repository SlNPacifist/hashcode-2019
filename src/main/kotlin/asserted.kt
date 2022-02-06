fun main(args: Array<String>) {
    // Path relative to src/main/kotlin/resources
    val solver = SolverFactory.createFromXmlResource<Album>("AssertedSolverConfig.xml").buildSolver()

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

    val album = Album.fromFileName(args[0])
    solver.solve(album)
}