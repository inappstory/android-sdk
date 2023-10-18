package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.filedownloader.AsyncFileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FontDownload extends AsyncFileDownload {
    public FontDownload(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull LruDiskCache cache,
            @NonNull ExecutorService service
    ) {
        super(url, fileDownloadCallback, cache, service);
    }

    @Override
    public String getCacheKey() {
        return deleteQueryArgumentsFromUrl(url);
    }

    @Override
    public String getDownloadFilePath() {
        return cache.getFileFromKey(getCacheKey()).getAbsolutePath();
    }

    @Override
    public void onProgress(long currentProgress, long max) {

    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public LruDiskCache getCache() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) return service.getCommonCache();
        return null;
    }
}
