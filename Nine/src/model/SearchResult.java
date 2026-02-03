package model;

import java.io.File;

/**
 * Represents a single search result with file, line number, and matching content.
 * Immutable class - thread-safe by design.
 */
public class SearchResult {
    private final File file;
    private final int lineNumber;
    private final String lineContent;
    
    public SearchResult(File file, int lineNumber, String lineContent) {
        this.file = file;
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    public File getFile() {
        return file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }
    
    @Override
    public String toString() {
        return String.format("%s:%d  %s", file.getName(), lineNumber, lineContent);
    }
}
