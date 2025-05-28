package com.inappstory.sdk.game.cache;

public class FilePathAndContent {
    public FilePathAndContent(String filePath, String fileContent) {
        this.filePath = filePath;
        this.fileContent = fileContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileContent() {
        return fileContent;
    }

    private String filePath;
    private String fileContent;
}
