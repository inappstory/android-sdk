package com.inappstory.sdk.utils;

public interface ProgressCallbackWithLog {
    void onProgress(String log, long loadedSize, long totalSize);

}
