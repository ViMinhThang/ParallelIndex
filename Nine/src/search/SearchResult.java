package search;

import java.io.File;

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
