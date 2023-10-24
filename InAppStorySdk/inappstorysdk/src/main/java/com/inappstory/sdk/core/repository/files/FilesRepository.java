package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.FileDownload;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.ProgressFileDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.FontDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GameResourceDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GameSplashDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.GoodsWidgetPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.HomeWidgetPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryFileDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;
import com.inappstory.sdk.stories.filedownloader.usecases.ZipArchiveDownload;

import java.io.File;
import java.util.HashMap;

public class FilesRepository implements IFilesRepository {
    private FilesRepositoryCacheStorage cacheStorage;

    private HashMap<String, FileDownload> currentUseCases = new HashMap<>();
    private FilesRepositoryThreadsStorage threadsStorage = new FilesRepositoryThreadsStorage();

    public FilesRepository(File cacheDir) {
        cacheStorage = new FilesRepositoryCacheStorage(cacheDir);
    }

    @Override
    public void getStoryPreview(
            @NonNull final String url,
            final IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new StoryPreviewDownload(
                                url,
                                cacheStorage.getFastCache(),
                                threadsStorage.getStoryPreviewDownloadThread()
                        );
                    }
                },
                fileDownloadCallback
        );
    }

    @Override
    public void getFont(
            @NonNull final String url,
            IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new FontDownload(
                                url,
                                cacheStorage.getFastCache(),
                                threadsStorage.getStoryPreviewDownloadThread()
                        );
                    }
                },
                fileDownloadCallback
        );
    }

    @Override
    public void getGameResource(
            @NonNull final String url,
            @NonNull final String cacheKey,
            @NonNull final String downloadPath,
            final IFileDownloadCallback fileDownloadCallback,
            @NonNull final IFileDownloadProgressCallback progressCallback,
            @NonNull final DownloadInterruption interruption
    ) {
        if (cacheStorage == null) return;
        getFileWithProgress(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new GameResourceDownload(
                                url,
                                cacheKey,
                                cacheStorage.getInfiniteCache(),
                                downloadPath,
                                interruption
                        );
                    }
                },
                fileDownloadCallback,
                progressCallback
        );
    }

    @Override
    public void getGameSplash(
            @NonNull final String url,
            final IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new GameSplashDownload(
                                url,
                                cacheStorage.getInfiniteCache(),
                                threadsStorage.getGameSplashDownloadThread()
                        );
                    }
                },
                fileDownloadCallback
        );
    }

    @Override
    public void getGoodsWidgetPreview(
            @NonNull final String url,
            final IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new GoodsWidgetPreviewDownload(
                                url,
                                cacheStorage.getInfiniteCache(),
                                threadsStorage.getGoodsWidgetPreviewDownloadThread()
                        );
                    }
                },
                fileDownloadCallback
        );
    }

    @Override
    public void getHomeWidgetPreview(
            @NonNull final String url,
            final IFileDownloadCallback fileDownloadCallback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new HomeWidgetPreviewDownload(
                                url,
                                cacheStorage.getCommonCache(),
                                threadsStorage.getHomeScreenWidgetPreviewDownloadThread()
                        );
                    }
                },
                fileDownloadCallback
        );
    }

    @Override
    public void getStoryFile(
            @NonNull final String url,
            IFileDownloadCallback callback
    ) {
        if (cacheStorage == null) return;
        getFile(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new StoryFileDownload(
                                url,
                                cacheStorage.getCommonCache()
                        );
                    }
                },
                callback
        );
    }

    @Override
    public void getZipArchive(
            @NonNull final String url,
            @NonNull final String downloadPath,
            @NonNull final IFileDownloadCallback fileDownloadCallback,
            @NonNull final IFileDownloadProgressCallback progressCallback,
            @NonNull final DownloadInterruption interruption
    ) {
        if (cacheStorage == null) return;
        getFileWithProgress(
                url,
                new IFileDownloadCreate() {
                    @Override
                    public FileDownload create() {
                        return new ZipArchiveDownload(
                                url,
                                downloadPath,
                                cacheStorage.getInfiniteCache(),
                                interruption
                        );
                    }
                },
                null,
                progressCallback
        );
    }

    private void getFileWithProgress(
            String url,
            IFileDownloadCreate creator,
            IFileDownloadCallback callback,
            IFileDownloadProgressCallback progressCallback
    ) {
        FileDownload downloadUseCase = currentUseCases.get(url);
        if (downloadUseCase == null) downloadUseCase = creator.create();
        try {
            ((downloadUseCase instanceof ProgressFileDownload) ?
                    ((ProgressFileDownload) downloadUseCase)
                            .addProgressCallback(progressCallback) :
                    downloadUseCase
            )
                    .addDownloadCallback(callback)
                    .downloadOrGetFromCache();
        } catch (Exception ignored) {
        }
    }

    private void getFile(
            String url,
            IFileDownloadCreate creator,
            IFileDownloadCallback callback
    ) {
        FileDownload downloadUseCase = currentUseCases.get(url);
        if (downloadUseCase == null) downloadUseCase = creator.create();
        try {
            downloadUseCase
                    .addDownloadCallback(callback)
                    .downloadOrGetFromCache();
        } catch (Exception ignored) {
        }
    }


    @Override
    public void clearCaches() {
        if (cacheStorage == null) return;
        cacheStorage.clearCaches();
    }
}
