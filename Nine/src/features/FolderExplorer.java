package features;

import model.Stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RecursiveAction;

public class FolderExplorer extends RecursiveAction {

    private final File folder;
    private final BlockingQueue<File> fileQueue;
    private final Stats stats;
    
    public FolderExplorer(File folder, BlockingQueue<File> fileQueue, Stats stats) {
        this.folder = folder;
        this.fileQueue = fileQueue;
        this.stats = stats;
    }

    @Override
    protected void compute() {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        List<FolderExplorer> subTasks = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                FolderExplorer task = new FolderExplorer(file, fileQueue, stats);
                stats.incrementForkCount();
                task.fork();
                subTasks.add(task);
            } else {
                fileQueue.offer(file);
            }
        }
        for (FolderExplorer task : subTasks) {
            task.join();
        }
    }
}
