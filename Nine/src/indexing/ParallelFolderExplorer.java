package indexing;

import model.Stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

public class ParallelFolderExplorer extends RecursiveAction {

    private final File folder;
    private final BlockingQueue<File> fileQueue;
    private final Stats stats;
    private final ConcurrentLinkedQueue<File> textFileCollector;

    public ParallelFolderExplorer(File folder, BlockingQueue<File> fileQueue, Stats stats) {
        this(folder, fileQueue, stats, null);
    }

    public ParallelFolderExplorer(File folder, BlockingQueue<File> fileQueue,
            Stats stats, ConcurrentLinkedQueue<File> textFileCollector) {
        this.folder = folder;
        this.fileQueue = fileQueue;
        this.stats = stats;
        this.textFileCollector = textFileCollector;
    }

    @Override
    protected void compute() {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        List<ParallelFolderExplorer> subTasks = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                ParallelFolderExplorer task = new ParallelFolderExplorer(
                        file, fileQueue, stats, textFileCollector);
                stats.incrementForkCount();
                task.fork();
                subTasks.add(task);
            } else {
                fileQueue.offer(file);
                if (textFileCollector != null && FileUtils.isTextFile(file)) {
                    textFileCollector.offer(file);
                }
            }
        }
        for (ParallelFolderExplorer task : subTasks) {
            task.join();
        }
    }
}
