package commands;

import benchmark.BenchmarkRunner;
import benchmark.BenchmarkTask;
import benchmark.BenchmarkResult;
import benchmark.FileIndexingBenchmark;
import benchmark.FileSearchBenchmark;
import benchmark.FolderTraversalBenchmark;
import indexing.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BenchmarkCommand implements Command {

    private static final int WARMUP_RUNS = 2;
    private static final int MEASURED_RUNS = 3;

    @Override
    public String getName() {
        return "benchmark";
    }

    @Override
    public String getDescription() {
        return "Compare sequential vs parallel performance";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        File rootFolder = ctx.getRootFolder();

        if (rootFolder == null) {
            System.out.println("Error: No root folder set. Use 'folder' command first.");
            return;
        }

        System.out.print("Enter search keyword for benchmark: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("Error: Keyword cannot be empty.");
            return;
        }

        List<File> textFiles = collectTextFiles(rootFolder);

        printHeader(rootFolder);

        BenchmarkRunner runner = new BenchmarkRunner(WARMUP_RUNS, MEASURED_RUNS);
        BenchmarkTask[] tasks = {
                new FolderTraversalBenchmark(rootFolder, ctx.getForkJoinPool()),
                new FileIndexingBenchmark(textFiles),
                new FileSearchBenchmark(textFiles, keyword, ctx.getForkJoinPool())
        };

        List<BenchmarkResult> results = new ArrayList<>();
        for (int i = 0; i < tasks.length; i++) {
            System.out.printf("[%d/%d] Benchmarking %s...%n", i + 1, tasks.length, tasks[i].getName());
            results.add(runner.run(tasks[i]));
        }

        System.out.println();
        printResults(results, ctx);
    }

    private void printHeader(File rootFolder) {
        System.out.println();
        System.out.println(repeatChar('=', 66));
        System.out.println("  BENCHMARK: Sequential vs Parallel");
        System.out.println("  Folder: " + rootFolder.getAbsolutePath());
        System.out.printf("  Warmup: %d runs | Measured: %d runs (averaged)%n", WARMUP_RUNS, MEASURED_RUNS);
        System.out.println(repeatChar('=', 66));
        System.out.println();
    }

    private void printResults(List<BenchmarkResult> results, AppContext ctx) {
        String border = repeatChar('=', 66);
        String divider = repeatChar('-', 22) + "+" + repeatChar('-', 13) + "+"
                + repeatChar('-', 11) + "+" + repeatChar('-', 9);

        System.out.println(border);
        System.out.printf("  BENCHMARK RESULTS (%d runs averaged)%n", MEASURED_RUNS);
        System.out.println(border);
        System.out.println("  Task                  | Sequential  | Parallel  | Speedup");
        System.out.println(divider);
        for (BenchmarkResult result : results) {
            System.out.println(result.toTableRow());
        }
        System.out.println(border);

        int cores = Runtime.getRuntime().availableProcessors();
        int parallelism = ctx.getForkJoinPool().getParallelism();
        System.out.printf("  System: %d cores | ForkJoinPool parallelism: %d%n", cores, parallelism);
        System.out.println(border);
    }

    private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private List<File> collectTextFiles(File folder) {
        List<File> files = new ArrayList<>();
        collectTextFilesRecursive(folder, files);
        return files;
    }

    private void collectTextFilesRecursive(File folder, List<File> files) {
        File[] children = folder.listFiles();
        if (children == null)
            return;

        for (File child : children) {
            if (child.isDirectory()) {
                collectTextFilesRecursive(child, files);
            } else if (FileUtils.isTextFile(child)) {
                files.add(child);
            }
        }
    }
}
