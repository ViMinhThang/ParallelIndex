package commands;

import java.util.Scanner;

public interface Command {
    String getName();
    String getDescription();
    void execute(AppContext ctx, Scanner scanner);
}
