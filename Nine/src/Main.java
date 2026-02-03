import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    
    private static final int NUM_CONSUMERS = 4;
    
    public static void main(String[] args) {
        // 1. Choose folder to scan (change this to test different folders)
        String folderPath = "C:\\Users\\huynh";  // <-- Change this!
        File rootFolder = new File(folderPath);
        
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            System.out.println("Error: Invalid folder path: " + folderPath);
            return;
        }
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(60));
        System.out.println("PARALLEL FILE INDEXER & SEARCH");
        System.out.println("=".repeat(60));
        System.out.println("1. Index files only");
        System.out.println("2. Index files and search");
        System.out.print("Choose option (1 or 2): ");
        
        int option = scanner.nextInt();
        scanner.nextLine();  // consume newline
        
        String searchKeyword = null;
        if (option == 2) {
            System.out.print("Enter search keyword: ");
            searchKeyword = scanner.nextLine();
        }
        
        System.out.println();
        System.out.println("Scanning folder: " + rootFolder.getAbsolutePath());
        System.out.println("Consumer threads: " + NUM_CONSUMERS);
        System.out.println();
        
        // 2. Create shared data structures
        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Integer> globalIndex = new ConcurrentHashMap<>();
        List<File> allFiles = new CopyOnWriteArrayList<>();  // For search feature
        
        // 3. Create Fork/Join pool for FolderExplorer (Producer)
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        
        // 4. Create thread pool for FileAnalyzer (Consumers)
        ExecutorService consumerPool = Executors.newFixedThreadPool(NUM_CONSUMERS);
        
        // 5. Start timing
        long startTime = System.currentTimeMillis();
        
        // 6. Start consumer threads FIRST (they will wait for items)
        System.out.println("[Starting] " + NUM_CONSUMERS + " consumer threads...");
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumerPool.submit(new FileAnalyzer(fileQueue, globalIndex));
        }
        
        // 7. Start producer (FolderExplorer) - this will populate the queue
        System.out.println("[Starting] FolderExplorer on " + rootFolder.getName());
        FolderExplorer explorer = new FolderExplorer(rootFolder, fileQueue);
        forkJoinPool.invoke(explorer);  // Blocks until all folders are scanned
        
        // Collect all files from queue for search (drain remaining + collect from index)
        fileQueue.drainTo(allFiles);
        
        System.out.println("[Done] Folder exploration complete!");
        System.out.println("[Sending] Poison pills to stop consumers...");
        
        // 8. Send poison pills to stop consumers
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            try {
                fileQueue.put(new File("ThisisExit"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // 9. Wait for consumers to finish
        consumerPool.shutdown();
        try {
            boolean finished = consumerPool.awaitTermination(60, TimeUnit.SECONDS);
            if (!finished) {
                System.out.println("Warning: Consumers did not finish in time!");
                consumerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 10. Stop timing for indexing
        long indexTime = System.currentTimeMillis() - startTime;
        
        // 11. Print indexing results
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("INDEXING RESULTS");
        System.out.println("=".repeat(60));
        System.out.println("Total unique files indexed: " + globalIndex.size());
        System.out.println("Indexing time: " + indexTime + " ms");
        System.out.println();
        
        // 12. Print extension breakdown
        System.out.println("--- Extension Breakdown ---");
        ConcurrentHashMap<String, Integer> extensions = new ConcurrentHashMap<>();
        for (String fileName : globalIndex.keySet()) {
            String ext = getExtension(fileName);
            extensions.merge(ext, 1, Integer::sum);
        }
        extensions.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(10)
            .forEach(e -> System.out.println("  " + e.getKey() + ": " + e.getValue() + " files"));
        
        // ============ SEARCH FEATURE ============
        if (option == 2 && searchKeyword != null) {
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.println("PARALLEL FILE SEARCH");
            System.out.println("=".repeat(60));
            System.out.println("Searching for: \"" + searchKeyword + "\"");
            System.out.println();
            
            // Collect all files for search (convert globalIndex keys to File objects)
            List<File> searchableFiles = new ArrayList<>();
            collectFilesRecursively(rootFolder, searchableFiles);
            
            System.out.println("Files to search: " + searchableFiles.size());
            
            // Start search timing
            long searchStart = System.currentTimeMillis();
            
            // Create and invoke FileSearcher
            FileSearcher searcher = new FileSearcher(searchableFiles, searchKeyword);
            List<SearchResult> results = forkJoinPool.invoke(searcher);
            
            long searchTime = System.currentTimeMillis() - searchStart;
            
            // Print search results
            System.out.println("Search completed in: " + searchTime + " ms");
            System.out.println("Matches found: " + results.size());
            System.out.println();
            
            if (results.isEmpty()) {
                System.out.println("No matches found for \"" + searchKeyword + "\"");
            } else {
                System.out.println("--- Search Results (first 20) ---");
                int count = 0;
                for (SearchResult result : results) {
                    if (count++ >= 20) {
                        System.out.println("... and " + (results.size() - 20) + " more matches");
                        break;
                    }
                    System.out.println("  " + result);
                }
            }
        }
        
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("COMPLETE!");
        System.out.println("=".repeat(60));
        
        // Cleanup
        forkJoinPool.shutdown();
        scanner.close();
    }
    
    private static String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "(no extension)";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }
    
    /**
     * Recursively collect all files (not directories) from a folder
     */
    private static void collectFilesRecursively(File folder, List<File> files) {
        File[] children = folder.listFiles();
        if (children == null) return;
        
        for (File child : children) {
            if (child.isDirectory()) {
                collectFilesRecursively(child, files);
            } else {
                // Only add text-readable files
                String name = child.getName().toLowerCase();
                if (name.endsWith(".java") || name.endsWith(".txt") || 
                    name.endsWith(".md") || name.endsWith(".xml") ||
                    name.endsWith(".json") || name.endsWith(".html") ||
                    name.endsWith(".css") || name.endsWith(".js") ||
                    name.endsWith(".properties") || name.endsWith(".yml") ||
                    name.endsWith(".yaml") || name.endsWith(".gradle")) {
                    files.add(child);
                }
            }
        }
    }
}
