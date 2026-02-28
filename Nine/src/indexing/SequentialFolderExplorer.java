package indexing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SequentialFolderExplorer {

    public static List<File> explore(File folder) {
        List<File> files = new ArrayList<>();
        exploreRecursive(folder, files);
        return files;
    }

    private static void exploreRecursive(File folder, List<File> files) {
        File[] children = folder.listFiles();
        if (children == null)
            return;

        for (File child : children) {
            if (child.isDirectory()) {
                exploreRecursive(child, files);
            } else {
                files.add(child);
            }
        }
    }
}
