package search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelFileSearcher extends RecursiveTask<List<SearchResult>> {

    private static final int THRESHOLD = 10;
    private final List<File> files;
    private final String keyword;
    private AtomicInteger querieCount;

    public ParallelFileSearcher(List<File> files, String keyword) {
        this.files = files;
        this.keyword = keyword;
        this.querieCount = new AtomicInteger(0);
    }

    @Override
    protected List<SearchResult> compute() {
        if (files.size() <= THRESHOLD) {
            return searchSequentially();
        }

        int mid = files.size() / 2;
        List<File> firstHalf = files.subList(0, mid);
        List<File> secondHalf = files.subList(mid, files.size());

        ParallelFileSearcher task1 = new ParallelFileSearcher(firstHalf, keyword);
        ParallelFileSearcher task2 = new ParallelFileSearcher(secondHalf, keyword);

        task1.fork();
        List<SearchResult> result2 = task2.compute();
        List<SearchResult> result1 = task1.join();

        List<SearchResult> allResults = new ArrayList<>(result1);
        allResults.addAll(result2);
        return allResults;
    }

    private List<SearchResult> searchSequentially() {
        List<SearchResult> results = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(file.toPath());

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    querieCount.addAndGet(1);
                    if (line.contains(keyword)) {
                        results.add(new SearchResult(file, i + 1, line.trim()));
                    }
                }
            } catch (IOException e) {
                // Skip files that can't be read
            }
        }

        return results;
    }

    public AtomicInteger getQuerieCount() {
        return querieCount;
    }
}
