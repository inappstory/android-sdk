package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;

public class StoryFileDownload extends FileDownload {
    public StoryFileDownload(
            String url,
            @NonNull LruDiskCache cache
    ) {
        super(url, new IFileDownloadCallback() {
            @Override
            public void onSuccess(String fileAbsolutePath) {

            }

            @Override
            public void onError(int errorCode, String error) {

            }
        }, cache);
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
