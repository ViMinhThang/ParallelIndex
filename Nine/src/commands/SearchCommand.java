package commands;

import search.ParallelFileSearcher;
import search.SearchResult;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class SearchCommand implements Command {

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "Search files by content (parallel)";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        List<File> files = ctx.getIndexedFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("Error: No files indexed. Run 'index' command first.");
            return;
        }

        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Error: Keyword cannot be empty.");
            return;
        }

        System.out.println();
        System.out.println("[Searching] \"" + keyword + "\" in " + files.size() + " files...");

        long startTime = System.currentTimeMillis();

        ParallelFileSearcher searcher = new ParallelFileSearcher(files, keyword);
        List<SearchResult> results = ctx.getForkJoinPool().invoke(searcher);

        long duration = System.currentTimeMillis() - startTime;
        ctx.getStats().recordSearch(duration);
        System.out.println("[Done] Found " + results.size() + " matches in " + duration + " ms");
        System.out.println();

        if (results.isEmpty()) {
            System.out.println("No matches found for \"" + keyword + "\"");
        } else {
            System.out.println("--- Results (first 20) ---");
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
}
