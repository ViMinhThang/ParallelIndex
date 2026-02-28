package benchmark;

import search.ParallelFileSearcher;
import search.SequentialFileSearcher;

import java.io.File;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class FileSearchBenchmark implements BenchmarkTask {

    private final List<File> files;
    private final String keyword;
    private final ForkJoinPool forkJoinPool;

    public FileSearchBenchmark(List<File> files, String keyword, ForkJoinPool forkJoinPool) {
        this.files = files;
        this.keyword = keyword;
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public String getName() {
        return "File Search";
    }

    @Override
    public void runSequential() {
        SequentialFileSearcher.search(files, keyword);
    }

    @Override
    public void runParallel() {
        forkJoinPool.invoke(new ParallelFileSearcher(files, keyword));
    }
}
