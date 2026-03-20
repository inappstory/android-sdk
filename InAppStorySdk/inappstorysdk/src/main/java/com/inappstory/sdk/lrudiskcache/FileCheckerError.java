package com.inappstory.sdk.lrudiskcache;

import androidx.annotation.NonNull;

public class FileCheckerError implements FileCheckerResult {
    private final String message;

    public FileCheckerError(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return message + "";
    }
}
