package com.inappstory.sdk.stories.cache;

public interface FileLoadProgressCallback {
    void onProgress(int loadedSize, int totalSize);
}
