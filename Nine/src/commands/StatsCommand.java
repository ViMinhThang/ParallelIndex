package commands;

import java.util.Scanner;

public class StatCommand implements Command{
    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void execute(AppContext ctx, Scanner scanner) {

    }
}
