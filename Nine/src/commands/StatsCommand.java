package commands;

import model.Stats;

import java.util.Scanner;

public class StatsCommand implements Command {
    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Show statistics from indexing and search";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        Stats stats = ctx.getStats();
        System.out.println();
        System.out.println("=".repeat(15) + " Stats " + "=".repeat(15));
        System.out.println("  Files indexed:        " + stats.getIndexedFiles());
        System.out.println("  Directories scanned:  " + stats.getNumOfDirs());
        System.out.println("  Fork count:           " + stats.getForkCount());
        System.out.println("  Total search queries: " + stats.getTotalSearchQueries());
        System.out.printf("  Average search time:  %.2f ms%n", stats.getAverageSearchTimeMs());
        System.out.println("=".repeat(37));
    }
}
