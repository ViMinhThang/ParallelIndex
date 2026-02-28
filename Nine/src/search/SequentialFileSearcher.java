package search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SequentialFileSearcher {

    public static List<SearchResult> search(List<File> files, String keyword) {
        List<SearchResult> results = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains(keyword)) {
                        results.add(new SearchResult(file, i + 1, lines.get(i).trim()));
                    }
                }
            } catch (IOException e) {
                // Skip files that can't be read
            }
        }

        return results;
    }
}
