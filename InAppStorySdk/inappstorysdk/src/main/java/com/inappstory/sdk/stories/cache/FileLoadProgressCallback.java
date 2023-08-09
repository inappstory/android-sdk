package com.inappstory.sdk.stories.cache;

import java.io.File;

public interface FileLoadProgressCallback {
    void onProgress(long loadedSize, long totalSize);
    void onSuccess(File file);
    void onError();
}
