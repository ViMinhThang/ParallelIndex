package commands;

import model.Stats;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class AppContext {
    private File rootFolder;
    private final ForkJoinPool forkJoinPool;
    private final ConcurrentHashMap<String, Integer> globalIndex;
    private List<File> indexedFiles;
    private Stats stats;
    public AppContext() {
        this.forkJoinPool = new ForkJoinPool();
        this.globalIndex = new ConcurrentHashMap<>();
        this.stats = new Stats();
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public ForkJoinPool getForkJoinPool() {
        return forkJoinPool;
    }

    public ConcurrentHashMap<String, Integer> getGlobalIndex() {
        return globalIndex;
    }

    public List<File> getIndexedFiles() {
        return indexedFiles;
    }

    public void setIndexedFiles(List<File> indexedFiles) {
        this.indexedFiles = indexedFiles;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
