package com.inappstory.sdk.stories.filedownloader;

public interface IFileDownloadProgressCallback {
    void onProgress(long currentProgress, long max);
}
