package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class ProgressFileDownload extends FileDownload {
    List<IFileDownloadProgressCallback> progressCallbacks = new ArrayList<>();
    private final Object progressLock = new Object();

    public ProgressFileDownload(
            @NonNull String url,
            @NonNull LruDiskCache cache
    ) {
        super(url, cache);
    }

    public FileDownload addProgressCallback(IFileDownloadProgressCallback progressCallback) {
        synchronized (progressLock) {
            progressCallbacks.add(progressCallback);
        }
        return this;
    }

    @Override
    protected void clearCallbacks() {
        super.clearCallbacks();
        synchronized (progressLock) {
            progressCallbacks.clear();
        }
    }

    @Override
    public void onProgress(long currentProgress, long max) {
        synchronized (progressLock) {
            for (IFileDownloadProgressCallback progressCallback : progressCallbacks) {
                progressCallback.onProgress(currentProgress, max);
            }
        }
    }
}
