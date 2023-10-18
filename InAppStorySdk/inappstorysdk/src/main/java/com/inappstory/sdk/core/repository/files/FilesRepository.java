package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.FontDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GameResourceDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GameSplashDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GoodsWidgetPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.HomeWidgetPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryFileDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.ZipArchiveDownload;

import java.io.File;

public class FilesRepository implements IFilesRepository {


    private FilesRepositoryCacheStorage cacheStorage;
    private FilesRepositoryThreadsStorage threadsStorage = new FilesRepositoryThreadsStorage();

    public FilesRepository(File cacheDir) {
        cacheStorage = new FilesRepositoryCacheStorage(cacheDir);
    }

    private DownloadFileState getFile(
            FileDownload downloadUseCase
    ) {
        try {
            return downloadUseCase.downloadOrGetFromCache();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void getStoryPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                new StoryPreviewDownload(
                        url,
                        fileDownloadCallback,
                        cacheStorage.getFastCache(),
                        threadsStorage.getStoryPreviewDownloadThread()
                )
        );
    }

    @Override
    public void getFont(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                new FontDownload(
                        url,
                        fileDownloadCallback,
                        cacheStorage.getCommonCache(),
                        threadsStorage.getFontDownloadThread()
                )
        );
    }

    @Override
    public DownloadFileState getGameResource(
            @NonNull String url,
            @NonNull String cacheKey,
            @NonNull String downloadPath,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    ) {
        if (cacheStorage == null) return null;
        return getFile(
                new GameResourceDownload(
                        url,
                        cacheKey,
                        cacheStorage.getInfiniteCache(),
                        downloadPath,
                        fileDownloadCallback,
                        progressCallback,
                        interruption
                )
        );
    }

    @Override
    public void getGameSplash(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                new GameSplashDownload(
                        url,
                        fileDownloadCallback,
                        cacheStorage.getInfiniteCache(),
                        threadsStorage.getGameSplashDownloadThread()
                )
        );
    }

    @Override
    public void getGoodsWidgetPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                new GoodsWidgetPreviewDownload(
                        url,
                        fileDownloadCallback,
                        cacheStorage.getInfiniteCache(),
                        threadsStorage.getGoodsWidgetPreviewDownloadThread()
                )
        );
    }

    @Override
    public void getHomeWidgetPreview(
            @NonNull String url,
            @NonNull IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                new HomeWidgetPreviewDownload(
                        url,
                        fileDownloadCallback,
                        cacheStorage.getCommonCache(),
                        threadsStorage.getHomeScreenWidgetPreviewDownloadThread()
                )
        );
    }

    @Override
    public DownloadFileState getStoryFile(@NonNull String url) {
        if (cacheStorage == null) return null;
        return getFile(
                new StoryFileDownload(
                        url,
                        cacheStorage.getCommonCache()
                )
        );
    }

    @Override
    public DownloadFileState getZipArchive(
            @NonNull String url,
            @NonNull String downloadPath,
            @NonNull IFileDownloadCallback fileDownloadCallback,
            @NonNull IFileDownloadProgressCallback progressCallback,
            @NonNull DownloadInterruption interruption
    ) {
        if (cacheStorage == null) return null;
        return getFile(
                new ZipArchiveDownload(
                        url,
                        downloadPath,
                        cacheStorage.getInfiniteCache(),
                        fileDownloadCallback,
                        progressCallback,
                        interruption
                )
        );
    }

    @Override
    public void clearCaches() {
        if (cacheStorage == null) return;
        cacheStorage.clearCaches();
    }
}
