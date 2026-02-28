package indexing;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String rootFolderPath;
    private final HashMap<String, Integer> globalIndex;
    private final List<String> indexedFilePaths;
    private final Map<String, Long> fileTimestamps;
    private final long createdAt;

    public IndexData(String rootFolderPath, HashMap<String, Integer> globalIndex,
            List<String> indexedFilePaths, Map<String, Long> fileTimestamps) {
        this.rootFolderPath = rootFolderPath;
        this.globalIndex = globalIndex;
        this.indexedFilePaths = indexedFilePaths;
        this.fileTimestamps = fileTimestamps;
        this.createdAt = System.currentTimeMillis();
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public HashMap<String, Integer> getGlobalIndex() {
        return globalIndex;
    }

    public List<String> getIndexedFilePaths() {
        return indexedFilePaths;
    }

    public Map<String, Long> getFileTimestamps() {
        return fileTimestamps;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<File> toFileList() {
        List<File> files = new java.util.ArrayList<>();
        for (String path : indexedFilePaths) {
            files.add(new File(path));
        }
        return files;
    }
}
