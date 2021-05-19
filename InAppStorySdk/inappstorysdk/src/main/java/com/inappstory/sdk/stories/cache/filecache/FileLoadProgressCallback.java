package com.inappstory.sdk.stories.cache.filecache;

public interface FileLoadProgressCallback {
    void onProgress(int loadedSize, int totalSize);
}
