package benchmark;

import indexing.ParallelFileAnalyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FileIndexingBenchmark implements BenchmarkTask {

    private static final int NUM_CONSUMERS = 4;

    private final List<File> files;

    public FileIndexingBenchmark(List<File> files) {
        this.files = files;
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

        ExecutorService consumerPool = Executors.newFixedThreadPool(NUM_CONSUMERS);
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerPool.submit(new ParallelFileAnalyzer(fileQueue, index));
        }

        for (File file : files) {
            fileQueue.offer(file);
        }

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
