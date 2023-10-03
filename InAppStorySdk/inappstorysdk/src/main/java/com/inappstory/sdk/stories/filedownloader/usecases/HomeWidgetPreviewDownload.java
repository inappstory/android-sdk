package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeWidgetPreviewDownload extends FileDownload {
    private static final ExecutorService getHomeWidgetPreviewThread = Executors.newFixedThreadPool(1);
    public HomeWidgetPreviewDownload(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        super(url, fileDownloadCallback);
    }

    @Override
    public DownloadFileState downloadOrGetFromCache() {
        getHomeWidgetPreviewThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HomeWidgetPreviewDownload.super.downloadOrGetFromCache();
                } catch (Exception exception) {
                    fileDownloadCallback.onError(-1, exception.getMessage());
                }
            }
        });
        return null;
    }

    @Override
    public String getCacheKey() {
        return url;
    }

    @Override
    public String getDownloadFilePath() {
        return getCache().getFileFromKey(getCacheKey()).getAbsolutePath();
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
