package commands;

import features.FileAnalyzer;
import features.FolderExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class IndexCommand implements Command {
    
    private static final int NUM_CONSUMERS = 4;
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
        
        System.out.println();
        System.out.println("[Indexing] " + rootFolder.getAbsolutePath());
        
        ctx.getStats().reset();
        
        // Create shared data structures
        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Integer> globalIndex = ctx.getGlobalIndex();
        globalIndex.clear();
        
        List<File> indexedFiles = new ArrayList<>();
        
        // Create consumer pool
        ExecutorService consumerPool = Executors.newFixedThreadPool(NUM_CONSUMERS);
        
        long startTime = System.currentTimeMillis();
        
        // Start consumers
        System.out.println("[Starting] " + NUM_CONSUMERS + " consumer threads...");
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerPool.submit(new FileAnalyzer(fileQueue, globalIndex));
        }
        
        // Start producer
        System.out.println("[Starting] FolderExplorer...");
        FolderExplorer explorer = new FolderExplorer(rootFolder, fileQueue, ctx.getStats());
        ctx.getForkJoinPool().invoke(explorer);
        
        // Collect indexed files for search
        collectFilesRecursively(ctx,rootFolder, indexedFiles);
        ctx.setIndexedFiles(indexedFiles);
        
        // Send poison pills
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            try {
                fileQueue.put(new File("ThisisExit"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Wait for consumers
        consumerPool.shutdown();
        try {
            consumerPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        // Update stats with final count
        ctx.getStats().addIndexedFiles(indexedFiles.size());
        // Print results
        System.out.println();
        System.out.println("[Done] Indexing complet!");
        System.out.println("  Files indexed: " + globalIndex.size());
        System.out.println("  Searchable files: " + indexedFiles.size());
        System.out.println("  Time: " + duration + " ms");
    }
    
    private void collectFilesRecursively(AppContext ctx,File folder, List<File> files) {
        File[] children = folder.listFiles();
        if (children == null) return;
        
        for (File child : children) {
            if (child.isDirectory()) {
                ctx.getStats().incrementDirs();
                collectFilesRecursively(ctx,child, files);
            } else {
                String name = child.getName().toLowerCase();
                if (name.endsWith(".java") || name.endsWith(".txt") || 
                    name.endsWith(".md") || name.endsWith(".xml") ||
                    name.endsWith(".json") || name.endsWith(".html") ||
                    name.endsWith(".css") || name.endsWith(".js") ||
                    name.endsWith(".properties") || name.endsWith(".yml")) {
                    files.add(child);
                }
            }
        }
    }
}
