package indexing;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

    private static final Set<String> TEXT_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".java", ".txt", ".md", ".xml", ".json",
            ".html", ".css", ".js", ".properties", ".yml"));

    public static boolean isTextFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : TEXT_EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
