package com.inappstory.sdk.core.repository.files;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.utils.lrudiskcache.FileManager;
import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.cache.DownloadInterruption;
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

    final File cacheDir;

    public FilesRepository(File cacheDir, int cacheSizeType) {
        cacheStorage = new FilesRepositoryCacheStorage(cacheDir, cacheSizeType);
        this.cacheDir = cacheDir;
    }

    @Override
    public LruDiskCache getInfiniteCache() {
        return cacheStorage.getInfiniteCache();
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
            final Long size,
            final String sha,
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
                                size,
                                sha,
                                null,
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
            final Long size,
            final String sha,
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
                                size,
                                sha,
                                null,
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
            @NonNull final String zipArchiveName,
            final Long size,
            final String sha,
            final Long totalFilesSize,
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
                                zipArchiveName,
                                size,
                                sha,
                                totalFilesSize,
                                cacheStorage.getInfiniteCache(),
                                interruption
                        );
                    }
                },
                null,
                progressCallback
        );
    }

    @Override
    public String getLocalStoryFile(@NonNull String url) {
        FileDownload downloadUseCase = new StoryFileDownload(
                url,
                cacheStorage.getCommonCache()
        );
        try {
            return downloadUseCase.getFromCache();
        } catch (Exception e) {
            throw null;
        }
    }

    @Override
    public String getLocalGameResource(@NonNull String url) {
        FileDownload downloadUseCase = new StoryFileDownload(
                url,
                cacheStorage.getInfiniteCache()
        );
        try {
            return downloadUseCase.getFromCache();
        } catch (Exception e) {
            throw null;
        }
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

    @Override
    public void removeOldGameFiles(String gameName, String newUrl) {

        File gameDir = new File(
                cacheStorage.getInfiniteCache().getCacheDir() +
                        File.separator + "zip" +
                        File.separator + gameName +
                        File.separator
        );
        if (!gameDir.getAbsolutePath().startsWith(
                cacheStorage.getInfiniteCache().getCacheDir() +
                        File.separator + "zip")) {
            return;
        }
        if (gameDir.exists() && gameDir.isDirectory()) {
            for (File gameDirFile : gameDir.listFiles()) {
                if (gameDirFile.getAbsolutePath().contains("" + newUrl.hashCode()))
                    continue;
                FileManager.deleteRecursive(gameDirFile);
            }
        }
    }
}
