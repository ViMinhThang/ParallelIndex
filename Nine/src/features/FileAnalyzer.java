import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FileAnalyzer implements Runnable {
    private final BlockingQueue<File> queue;
    private final ConcurrentHashMap<String, Integer> globalIndex;
    private final static String POISON_PILL = "ThisisExit";
    public FileAnalyzer(BlockingQueue<File> queue, ConcurrentHashMap<String, Integer> globalIndex) {
        this.queue = queue;
        this.globalIndex = globalIndex;
    }

    @Override
    public void run() {
        try {
            while (true) {
                File file = queue.take();
                if (isPoisonPill(file)) {
                    break;
                }
                indexFile(file);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void indexFile(File file) {
        globalIndex.compute(file.getName(), (key, val) -> (val == null) ? 1 : val + 1);
    }
    public boolean isPoisonPill(File file){
        boolean isNameEqual = file.getName().equals(POISON_PILL);

        return isNameEqual;
    }
}
