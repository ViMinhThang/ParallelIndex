package commands;

import indexing.IndexData;
import indexing.IndexSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class SaveCommand implements Command {

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Save the current index to disk";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        File rootFolder = ctx.getRootFolder();

        if (rootFolder == null) {
            System.out.println("Error: No root folder set. Use 'folder' command first.");
            return;
        }

        List<File> indexedFiles = ctx.getIndexedFiles();
        ConcurrentHashMap<String, Integer> globalIndex = ctx.getGlobalIndex();

        if (indexedFiles == null || indexedFiles.isEmpty() || globalIndex.isEmpty()) {
            System.out.println("Error: No index in memory. Run 'index' command first.");
            return;
        }

        long startTime = System.currentTimeMillis();

        List<String> filePaths = new ArrayList<>();
        Map<String, Long> timestamps = new HashMap<>();
        for (File file : indexedFiles) {
            String path = file.getAbsolutePath();
            filePaths.add(path);
            timestamps.put(path, file.lastModified());
        }

        HashMap<String, Integer> indexCopy = new HashMap<>(globalIndex);
        IndexData data = new IndexData(rootFolder.getAbsolutePath(), indexCopy, filePaths, timestamps);

        File outputFile = IndexSerializer.getIndexFile(rootFolder);

        try {
            IndexSerializer.save(data, outputFile);
            long duration = System.currentTimeMillis() - startTime;

            System.out.println();
            System.out.println("[Done] Index saved!");
            System.out.println("  Files:    " + filePaths.size());
            System.out.println("  Location: " + outputFile.getAbsolutePath());
            System.out.println("  Time:     " + duration + " ms");
        } catch (Exception e) {
            System.out.println("Error: Failed to save index — " + e.getMessage());
        }
    }
}
