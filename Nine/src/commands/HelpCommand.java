package commands;

import java.util.List;
import java.util.Scanner;

public class HelpCommand implements Command {
    
    private final List<Command> allCommands;
    
    public HelpCommand(List<Command> allCommands) {
        this.allCommands = allCommands;
    }
    
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public String getDescription() {
        return "Show available commands";
    }
    
    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("-".repeat(40));
        for (Command cmd : allCommands) {
            System.out.printf("  %-12s - %s%n", cmd.getName(), cmd.getDescription());
        }
        System.out.println("-".repeat(40));
    }
}
