package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;

public class ZipArchiveDownload extends FileDownload {
    IFileDownloadProgressCallback progressCallback;
    String downloadPath;
    DownloadInterruption interruption;

    public ZipArchiveDownload(
            @NonNull String url,
            @NonNull String downloadPath,
            @NonNull LruDiskCache cache,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    ) {
        super(url, fileDownloadCallback, cache);
        this.progressCallback = progressCallback;
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
    public void onProgress(long currentProgress, long max) {
        this.progressCallback.onProgress(currentProgress, max);
    }

    @Override
    public boolean isInterrupted() {
        return interruption.active;
    }

    @Override
    public LruDiskCache getCache() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) return service.getInfiniteCache();
        return null;
    }
}
