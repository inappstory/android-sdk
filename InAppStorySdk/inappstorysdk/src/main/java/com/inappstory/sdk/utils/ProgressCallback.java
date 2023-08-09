package com.inappstory.sdk.utils;

public interface ProgressCallback {
    void onProgress(long loadedSize, long totalSize);
}
