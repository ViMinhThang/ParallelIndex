package model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Stats {
    private final AtomicInteger indexedFiles = new AtomicInteger(0);
    private final AtomicInteger numOfDirs = new AtomicInteger(0);
    private final AtomicInteger forkCount = new AtomicInteger(0);
    private final AtomicInteger totalSearchQueries = new AtomicInteger(0);
    private final AtomicLong totalSearchTimeMs = new AtomicLong(0);

    public void incrementIndexedFiles() {
        indexedFiles.incrementAndGet();
    }
    
    public void addIndexedFiles(int count) {
        indexedFiles.addAndGet(count);
    }
    
    public void incrementDirs() {
        numOfDirs.incrementAndGet();
    }
    
    public void addDirs(int count) {
        numOfDirs.addAndGet(count);
    }
    
    public void incrementForkCount() {
        forkCount.incrementAndGet();
    }
    
    public void recordSearch(long timeMs) {
        totalSearchQueries.incrementAndGet();
        totalSearchTimeMs.addAndGet(timeMs);
    }
    
    // Getters
    public int getIndexedFiles() {
        return indexedFiles.get();
    }
    
    public int getNumOfDirs() {
        return numOfDirs.get();
    }
    
    public int getForkCount() {
        return forkCount.get();
    }
    
    public int getTotalSearchQueries() {
        return totalSearchQueries.get();
    }
    
    public double getAverageSearchTimeMs() {
        int queries = totalSearchQueries.get();
        if (queries == 0) return 0;
        return (double) totalSearchTimeMs.get() / queries;
    }
    
    public void reset() {
        indexedFiles.set(0);
        numOfDirs.set(0);
        forkCount.set(0);
        totalSearchQueries.set(0);
        totalSearchTimeMs.set(0);
    }
}
