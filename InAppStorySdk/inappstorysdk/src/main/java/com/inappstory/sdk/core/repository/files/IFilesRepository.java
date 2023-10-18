package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;

public interface IFilesRepository {
    void getStoryPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    );

    void getFont(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    );

    DownloadFileState getGameResource(
            @NonNull String url,
            @NonNull String cacheKey,
            @NonNull String downloadPath,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    void getGameSplash(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    );

    void getGoodsWidgetPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    );

    void getHomeWidgetPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    );

    DownloadFileState getStoryFile(
            @NonNull String url
    );

    DownloadFileState getZipArchive(
            @NonNull String url,
            @NonNull String downloadPath,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    void clearCaches();
}
