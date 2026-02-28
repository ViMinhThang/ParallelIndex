package commands;

import indexing.IndexData;
import indexing.IndexSerializer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class LoadCommand implements Command {

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Load a saved index from disk";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        File rootFolder = ctx.getRootFolder();

        if (rootFolder == null) {
            System.out.println("Error: No root folder set. Use 'folder' command first.");
            return;
        }

        File indexFile = IndexSerializer.getIndexFile(rootFolder);
        if (!indexFile.exists()) {
            System.out.println("Error: No saved index found at " + indexFile.getAbsolutePath());
            System.out.println("Run 'index' then 'save' first.");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            IndexData data = IndexSerializer.load(indexFile);

            // Check staleness
            List<String> staleFiles = IndexSerializer.checkStaleness(data);

            // Restore index into AppContext
            ConcurrentHashMap<String, Integer> restoredIndex = new ConcurrentHashMap<>(data.getGlobalIndex());
            ctx.getGlobalIndex().clear();
            ctx.getGlobalIndex().putAll(restoredIndex);

            List<File> restoredFiles = data.toFileList();
            ctx.setIndexedFiles(restoredFiles);

            long duration = System.currentTimeMillis() - startTime;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String savedDate = sdf.format(new Date(data.getCreatedAt()));

            System.out.println();
            System.out.println("[Done] Index loaded!");
            System.out.println("  Files:      " + restoredFiles.size());
            System.out.println("  Saved at:   " + savedDate);
            System.out.println("  Load time:  " + duration + " ms");

            if (!staleFiles.isEmpty()) {
                System.out.println();
                System.out.println("[Warning] " + staleFiles.size() + " files changed since last save:");
                int shown = 0;
                for (String stale : staleFiles) {
                    if (shown++ >= 10) {
                        System.out.println("  ... and " + (staleFiles.size() - 10) + " more");
                        break;
                    }
                    System.out.println("  - " + stale);
                }
                System.out.println("Consider running 'index' again for accurate results.");
            } else {
                System.out.println("  Status:     Up to date");
            }
        } catch (Exception e) {
            System.out.println("Error: Failed to load index — " + e.getMessage());
        }
    }
}
