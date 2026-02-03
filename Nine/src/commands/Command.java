package commands;

import java.util.Scanner;

public interface ommand {
    String getName();
    String getDescription();
    void execute(AppContext ctx, Scanner scanner);
}
