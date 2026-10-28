package com.inappstory.sdk.game.cache;

public class InGameResourceDownloadError implements InGameResourceDownloadResult {
    private final String message;

    public InGameResourceDownloadError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
