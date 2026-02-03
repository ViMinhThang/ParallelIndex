package commands;

import java.util.Scanner;

public class ExitCommand implements Command {
    
    @Override
    public String getName() {
        return "exit";
    }
    
    @Override
    public String getDescription() {
        return "Exit the program";
    }
    
    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        System.out.println("Goodbye!");
        ctx.getForkJoinPool().shutdown();
        System.exit(0);
    }
}
