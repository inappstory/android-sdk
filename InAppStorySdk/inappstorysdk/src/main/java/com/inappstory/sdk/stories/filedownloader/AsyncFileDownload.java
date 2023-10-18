package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.util.concurrent.ExecutorService;

public abstract class AsyncFileDownload extends FileDownload {

    private ExecutorService service;
    public AsyncFileDownload(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull LruDiskCache cache,
            @NonNull ExecutorService service
    ) {
        super(url, fileDownloadCallback, cache);
        this.service = service;
    }

    @Override
    public DownloadFileState downloadOrGetFromCache() throws Exception {
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    AsyncFileDownload.super.downloadOrGetFromCache();
                } catch (Exception exception) {
                    fileDownloadCallback.onError(-1, exception.getMessage());
                }
            }
        });
        return null;
    }
}
