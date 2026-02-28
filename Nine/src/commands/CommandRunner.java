package commands;

import java.util.*;

public class CommandRunner {
    private final List<Command> commands;
    private final Map<String, Command> commandMap;
    private final AppContext ctx;
    private final Scanner scanner;

    public CommandRunner() {
        this.commands = new ArrayList<>();
        this.commandMap = new HashMap<>();
        this.ctx = new AppContext();
        this.scanner = new Scanner(System.in);

        registerCommands();
    }

    private void registerCommands() {
        // Register all available commands
        addCommand(new FolderCommand());
        addCommand(new IndexCommand());
        addCommand(new SearchCommand());
        addCommand(new HelpCommand(commands));
        addCommand(new ExitCommand());
        addCommand(new StatsCommand());
        addCommand(new BenchmarkCommand());
        addCommand(new SaveCommand());
        addCommand(new LoadCommand());
    }

    private void addCommand(Command cmd) {
        commands.add(cmd);
        commandMap.put(cmd.getName().toLowerCase(), cmd);
    }

    public void run() {
        printWelcome();

        while (true) {
            System.out.println();
            System.out.print("Enter command (or 'help'): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.isEmpty()) {
                continue;
            }

            Command cmd = commandMap.get(input);
            if (cmd != null) {
                cmd.execute(ctx, scanner);
            } else {
                System.out.println("Unknown command: " + input);
                System.out.println("Type 'help' to see available commands.");
            }
        }
    }

    private void printWelcome() {
        System.out.println("=".repeat(50));
        System.out.println("  PARALLEL FILE INDEXER v1.0");
        System.out.println("  A parallel/multithreaded file search tool");
        System.out.println("=".repeat(50));
        System.out.println();
        System.out.println("Commands: folder, index, search, save, load, benchmark, stats, help, exit");
        System.out.println();
        System.out.println("Quick start:");
        System.out.println("  1. Use 'folder' to set the directory to scan");
        System.out.println("  2. Use 'index' to scan all files");
        System.out.println("  3. Use 'search' to find content in files");
        System.out.println("  4. Use 'save' to save the index to disk");
        System.out.println("  5. Use 'load' to load a saved index from disk");
        System.out.println("  6. Use 'benchmark' to compare sequential vs parallel performance");
        System.out.println("  7. Use 'stats' to print the statistics of the cli");

    }
}
