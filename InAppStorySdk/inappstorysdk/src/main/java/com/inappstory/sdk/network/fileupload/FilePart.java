package com.inappstory.sdk.network.fileupload;

public class FilePart {
    public String fieldName() {
        return fieldName;
    }

    public String filePath() {
        return filePath;
    }

    private final String fieldName;
    private final String filePath;

    public FilePart(String fieldName, String filePath) {
        this.fieldName = fieldName;
        this.filePath = filePath;
    }
}
