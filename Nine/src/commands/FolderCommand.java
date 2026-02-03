package commands;

import java.io.File;
import java.util.Scanner;

public class FolderCommand implements Command {
    
    private static final String DEFAULT_FOLDER = "C:\\Users\\huynh";
    
    @Override
    public String getName() {
        return "folder";
    }
    
    @Override
    public String getDescription() {
        return "Set or show the root folder to scan";
    }
    
    @Override
    public void execute(AppContext ctx, Scanner scanner) {
        File current = ctx.getRootFolder();
        
        if (current != null) {
            System.out.println("Current folder: " + current.getAbsolutePath());
        } else {
            System.out.println("No folder set. Default: " + DEFAULT_FOLDER);
        }
        
        System.out.print("Enter new folder path (or press Enter for default): ");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            if (current == null) {
                // Use default folder
                File defaultFolder = new File(DEFAULT_FOLDER);
                if (defaultFolder.exists() && defaultFolder.isDirectory()) {
                    ctx.setRootFolder(defaultFolder);
                    System.out.println("Using default folder: " + DEFAULT_FOLDER);
                } else {
                    System.out.println("Error: Default folder does not exist.");
                }
            }
            return;
        }
        
        File newFolder = new File(input);
        if (!newFolder.exists()) {
            System.out.println("Error: Folder does not exist: " + input);
            return;
        }
        if (!newFolder.isDirectory()) {
            System.out.println("Error: Not a directory: " + input);
            return;
        }
        
        ctx.setRootFolder(newFolder);
        System.out.println("Root folder set to: " + newFolder.getAbsolutePath());
    }
}
