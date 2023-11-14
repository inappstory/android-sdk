package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;

public interface IFilesRepository {
    LruDiskCache getInfiniteCache();

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
            Long size,
            String sha,
            IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    void getGameSplash(
            @NonNull String url,
            Long size,
            String sha,
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
            @NonNull String zipArchiveName,
            Long size,
            String sha,
            Long totalFilesSize,
            IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    );

    String getLocalStoryFile(@NonNull String url);

    String getLocalGameResource(@NonNull String url);

    void clearCaches();

    void removeOldGameFiles(String gameName, String newUrl);
}
