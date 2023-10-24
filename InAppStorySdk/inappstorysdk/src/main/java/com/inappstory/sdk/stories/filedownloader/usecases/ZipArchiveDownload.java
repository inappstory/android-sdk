package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.ProgressFileDownload;

public final class ZipArchiveDownload extends ProgressFileDownload {
    String downloadPath;
    DownloadInterruption interruption;

    public ZipArchiveDownload(
            @NonNull String url,
            @NonNull String downloadPath,
            @NonNull LruDiskCache cache,
            @NonNull DownloadInterruption interruption
    ) {
        super(url, cache);
        this.downloadPath = downloadPath;
        this.interruption = interruption;
    }

    @Override
    public String getCacheKey() {
        return deleteQueryArgumentsFromUrl(url);
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
