fun main() {
    PlannerBenchmarkFactory.createFromXmlResource("Benchmark.xml")
        .buildPlannerBenchmark()
        .benchmarkAndShowReportInBrowser()
}
