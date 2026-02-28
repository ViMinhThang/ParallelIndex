package commands;

import indexing.ParallelFileAnalyzer;
import indexing.ParallelFolderExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class IndexCommand implements Command {

    @Override
    public String getName() {
        return "index";
    }

    @Override
    public String getDescription() {
        return "Scan and index all files in the root folder";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        File rootFolder = ctx.getRootFolder();

        if (rootFolder == null) {
            System.out.println("Error: No root folder set. Use 'folder' command first.");
            return;
        }

        int numConsumers = Runtime.getRuntime().availableProcessors();

        System.out.println();
        System.out.println("[Indexing] " + rootFolder.getAbsolutePath());

        ctx.getStats().reset();

        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Integer> globalIndex = ctx.getGlobalIndex();
        globalIndex.clear();

        ConcurrentLinkedQueue<File> textFileCollector = new ConcurrentLinkedQueue<>();

        ExecutorService consumerPool = Executors.newFixedThreadPool(numConsumers);

        long startTime = System.currentTimeMillis();

        System.out.println("[Starting] " + numConsumers + " consumer threads...");
        for (int i = 0; i < numConsumers; i++) {
            consumerPool.submit(new ParallelFileAnalyzer(fileQueue, globalIndex));
        }

        System.out.println("[Starting] ParallelFolderExplorer...");
        ParallelFolderExplorer explorer = new ParallelFolderExplorer(
                rootFolder, fileQueue, ctx.getStats(), textFileCollector);
        ctx.getForkJoinPool().invoke(explorer);

        List<File> indexedFiles = new ArrayList<>(textFileCollector);
        ctx.setIndexedFiles(indexedFiles);

        for (int i = 0; i < numConsumers; i++) {
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

        long duration = System.currentTimeMillis() - startTime;
        ctx.getStats().addIndexedFiles(indexedFiles.size());
        System.out.println();
        System.out.println("[Done] Indexing complete!");
        System.out.println("  Files indexed: " + globalIndex.size());
        System.out.println("  Searchable files: " + indexedFiles.size());
        System.out.println("  Time: " + duration + " ms");
    }
}
