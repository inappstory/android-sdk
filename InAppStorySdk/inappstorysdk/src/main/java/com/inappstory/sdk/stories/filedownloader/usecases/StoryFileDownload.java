package com.inappstory.sdk.stories.filedownloader.usecases;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;

public class StoryFileDownload extends FileDownload {
    public StoryFileDownload(
            String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        super(url, fileDownloadCallback);
    }

    @Override
    public String getCacheKey() {
        return deleteQueryArgumentsFromUrl(url);
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
