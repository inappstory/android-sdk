package com.inappstory.sdk.stories.cache;

import java.io.File;

public interface FileLoadProgressCallback {
    void onProgress(int loadedSize, int totalSize);
    void onSuccess(File file);
    void onError();
}
