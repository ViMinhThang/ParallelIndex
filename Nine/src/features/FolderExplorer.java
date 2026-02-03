import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RecursiveAction;

public class FolderExplorer extends RecursiveAction {

    private final File folder;
    private final BlockingQueue<File> fileQueue;

    public FolderExplorer(File folder, BlockingQueue fileQueue) {
        this.folder = folder;
        this.fileQueue = fileQueue;
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
                FolderExplorer task = new FolderExplorer(file, fileQueue);
                task.fork();
                subTasks.add(task);
            } else {
                fileQueue.offer(file);
            }
        }
        for (FolderExplorer task:subTasks){
            task.join();
        }
    }
}
