package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;

public interface IFilesRepository {
    void getStoryPreview(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getFont(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getGameResource(
            @NonNull String url,
            @NonNull String cacheKey,
            @NonNull String downloadPath,
            IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    void getGameSplash(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getGoodsWidgetPreview(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getHomeWidgetPreview(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getStoryFile(
            @NonNull String url,
            IFileDownloadCallback fileDownloadCallback
    );

    void getZipArchive(
            @NonNull String url,
            @NonNull String downloadPath,
            IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    String getLocalStoryFile(@NonNull String url);

    void clearCaches();
}
