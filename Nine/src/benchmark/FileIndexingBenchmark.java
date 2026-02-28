package benchmark;

import indexing.ParallelFileAnalyzer;
import indexing.ParallelFolderExplorer;
import model.Stats;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FileIndexingBenchmark implements BenchmarkTask {

    private static final int NUM_CONSUMERS = 4;

    private final File rootFolder;
    private final List<File> files;
    private final ForkJoinPool forkJoinPool;

    public FileIndexingBenchmark(File rootFolder, List<File> files, ForkJoinPool forkJoinPool) {
        this.rootFolder = rootFolder;
        this.files = files;
        this.forkJoinPool = forkJoinPool;
    }

    @Override
    public String getName() {
        return "File Indexing";
    }

    @Override
    public void runSequential() {
        Map<String, Integer> index = new HashMap<>();
        for (File file : files) {
            String name = file.getName();
            Integer val = index.get(name);
            index.put(name, (val == null) ? 1 : val + 1);
        }
    }

    @Override
    public void runParallel() {
        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Integer> index = new ConcurrentHashMap<>();
        Stats tempStats = new Stats();

        ExecutorService consumerPool = Executors.newFixedThreadPool(NUM_CONSUMERS);
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerPool.submit(new ParallelFileAnalyzer(fileQueue, index));
        }

        ParallelFolderExplorer explorer = new ParallelFolderExplorer(rootFolder, fileQueue, tempStats);
        forkJoinPool.invoke(explorer);

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            try {
                fileQueue.put(new File("ThisisExit"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        consumerPool.shutdown();
        try {
            consumerPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
