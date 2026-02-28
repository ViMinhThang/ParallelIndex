package benchmark;

public class BenchmarkResult {
    private final String taskName;
    private final long sequentialTimeMs;
    private final long parallelTimeMs;

    public BenchmarkResult(String taskName, long sequentialTimeMs, long parallelTimeMs) {
        this.taskName = taskName;
        this.sequentialTimeMs = sequentialTimeMs;
        this.parallelTimeMs = parallelTimeMs;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getSequentialTimeMs() {
        return sequentialTimeMs;
    }

    public long getParallelTimeMs() {
        return parallelTimeMs;
    }

    public double getSpeedup() {
        if (parallelTimeMs == 0)
            return 0;
        return (double) sequentialTimeMs / parallelTimeMs;
    }

    public String toTableRow() {
        return String.format("  %-20s | %8d ms | %7d ms |  %.2fx",
                taskName, sequentialTimeMs, parallelTimeMs, getSpeedup());
    }
}
