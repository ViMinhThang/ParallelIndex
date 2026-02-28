package benchmark;

public class BenchmarkRunner {

    private final int warmupRuns;
    private final int measuredRuns;

    public BenchmarkRunner(int warmupRuns, int measuredRuns) {
        this.warmupRuns = warmupRuns;
        this.measuredRuns = measuredRuns;
    }

    public BenchmarkResult run(BenchmarkTask task) {
        for (int i = 0; i < warmupRuns; i++) {
            task.runSequential();
            task.runParallel();
        }

        long seqTotal = 0;
        for (int i = 0; i < measuredRuns; i++) {
            long start = System.currentTimeMillis();
            task.runSequential();
            seqTotal += System.currentTimeMillis() - start;
        }

        long parTotal = 0;
        for (int i = 0; i < measuredRuns; i++) {
            long start = System.currentTimeMillis();
            task.runParallel();
            parTotal += System.currentTimeMillis() - start;
        }

        long seqAvg = seqTotal / measuredRuns;
        long parAvg = parTotal / measuredRuns;

        System.out.println("      Sequential: " + seqAvg + " ms | Parallel: " + parAvg + " ms");
        return new BenchmarkResult(task.getName(), seqAvg, parAvg);
    }
}
