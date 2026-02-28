package benchmark;

public interface BenchmarkTask {
    String getName();

    void runSequential();

    void runParallel();
}
