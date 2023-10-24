package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.ProgressFileDownload;

public final class GameResourceDownload extends ProgressFileDownload {
    String downloadPath;
    String cacheKey;
    DownloadInterruption interruption;

    public GameResourceDownload(
            @NonNull String url,
            @NonNull String cacheKey,
            @NonNull LruDiskCache cache,
            @NonNull String downloadPath,
            @NonNull DownloadInterruption interruption
    ) {
        super(url, cache);
        this.cacheKey = cacheKey;
        this.downloadPath = downloadPath;
        this.interruption = interruption;
    }

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public String getDownloadFilePath() {
        return downloadPath;
    }


    @Override
    public boolean isInterrupted() {
        return interruption.active;
    }
}
