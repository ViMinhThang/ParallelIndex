package indexing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IndexSerializer {

    private static final String INDEX_FILE_NAME = ".parallelindex.pidx";

    public static File getIndexFile(File rootFolder) {
        return new File(rootFolder, INDEX_FILE_NAME);
    }

    public static void save(IndexData data, File outputFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            oos.writeObject(data);
        }
    }

    public static IndexData load(File inputFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {
            return (IndexData) ois.readObject();
        }
    }

    public static List<String> checkStaleness(IndexData data) {
        List<String> staleFiles = new ArrayList<>();
        for (String path : data.getFileTimestamps().keySet()) {
            File file = new File(path);
            if (!file.exists()) {
                staleFiles.add(path + " (deleted)");
            } else {
                long savedTimestamp = data.getFileTimestamps().get(path);
                long currentTimestamp = file.lastModified();
                if (currentTimestamp != savedTimestamp) {
                    staleFiles.add(path + " (modified)");
                }
            }
        }
        return staleFiles;
    }
}
