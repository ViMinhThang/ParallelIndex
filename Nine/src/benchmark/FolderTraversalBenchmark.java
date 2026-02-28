package benchmark;

import indexing.ParallelFolderExplorer;
import indexing.SequentialFolderExplorer;
import model.Stats;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

public class FolderTraversalBenchmark implements BenchmarkTask {

    private final File rootFolder;
    private final ForkJoinPool forkJoinPool;

    public FolderTraversalBenchmark(File rootFolder, ForkJoinPool forkJoinPool) {
        this.rootFolder = rootFolder;
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public String getName() {
        return "Folder Traversal";
    }

    @Override
    public void runSequential() {
        SequentialFolderExplorer.explore(rootFolder);
    }

    @Override
    public void runParallel() {
        BlockingQueue<File> tempQueue = new LinkedBlockingQueue<>();
        Stats tempStats = new Stats();
        ParallelFolderExplorer explorer = new ParallelFolderExplorer(rootFolder, tempQueue, tempStats);
        forkJoinPool.invoke(explorer);
    }
}
