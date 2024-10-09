package com.inappstory.sdk.stories.cache.usecases;

import static java.util.UUID.randomUUID;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;

public class ArchiveUseCase extends GetCacheFileUseCase<Void> {
    private final String url;
    private final String archiveName;
    private final String archiveSha1;
    private final String type = "Archive";
    private final long totalFilesSize;
    private final long archiveSize;
    private final ProgressCallback progressCallback;
    private final DownloadInterruption interruption;
    private final UseCaseCallback<File> useCaseCallback;
    private final FileChecker fileChecker = new FileChecker();

    public ArchiveUseCase(
            IASCore core,
            String url,
            long archiveSize,
            String archiveSha1,
            long totalFilesSize,
            ProgressCallback progressCallback,
            DownloadInterruption interruption,
            UseCaseCallback<File> useCaseCallback
    ) {
        super(core);
        this.url = url;
        this.totalFilesSize = totalFilesSize;
        this.uniqueKey = StringsUtils.md5(url);
        this.archiveName = getArchiveName(url);
        this.archiveSize = archiveSize;
        this.archiveSha1 = archiveSha1;
        this.progressCallback = progressCallback;
        this.interruption = interruption;
        this.useCaseCallback = useCaseCallback;
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip" +
                File.separator +
                archiveName +
                File.separator +
                uniqueKey +
                Downloader.getFileExtensionFromUrl(url);
    }

    private String getArchiveName(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        String[] nameParts = fName.split("_");
        if (nameParts.length > 0) return nameParts[0];
        return "";
    }

    @Override
    @WorkerThread
    public Void getFile() {
        if (!getLocalArchive()) {
            downloadArchive();
        }
        return null;
    }

    private boolean getLocalArchive() {
        LruDiskCache cache = getCache();
        File cachedArchive = cache.getFullFile(uniqueKey, type);

        downloadLog.generateRequestLog(url);
        if (cachedArchive != null) {
            if (!fileChecker.checkWithShaAndSize(
                    cachedArchive,
                    archiveSize,
                    archiveSha1,
                    true
            )) {
                File directory = new File(
                        cachedArchive.getParent() +
                                File.separator + uniqueKey);
                try {
                    cache.delete(uniqueKey);
                    if (directory.exists()) {
                        FileManager.deleteRecursive(directory);
                    }
                } catch (Exception e) {

                }
                useCaseCallback.onError(null);
                return false;
            } else {
                useCaseCallback.onSuccess(cachedArchive);
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                return true;
            }
        } else {
            removeOldVersions();
        }
        return false;
    }

    private void removeOldVersions() {
        File gameDir = new File(
                getCache().getCacheDir().getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "zip" +
                        File.separator +
                        archiveName +
                        File.separator
        );
        if (!gameDir.getAbsolutePath().startsWith(
                getCache().getCacheDir().getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "zip")
        ) {
            return;
        }
        if (gameDir.exists() && gameDir.isDirectory()) {
            File[] files = gameDir.listFiles();
            if (files == null) return;
            for (File gameDirFile : files) {
                FileManager.deleteRecursive(gameDirFile);
            }
        }
    }

    private void downloadArchive() {
        downloadLog.sendRequestLog();
        if (!filePath.startsWith(
                getCache().getCacheDir().getAbsolutePath() +
                        File.separator +
                        "v2" +
                        File.separator +
                        "zip")
        ) {
            useCaseCallback.onError("Error in game name");
            return;
        }
        if (totalFilesSize > getCache().getCacheDir().getFreeSpace()) {
            useCaseCallback.onError("No free space for download");
            return;
        }
        final String hash = randomUUID().toString();
        DownloadFileState fileState = getCache().get(uniqueKey);
        long offset = 0;
        if (fileState != null) {
            offset = fileState.downloadedSize;
        }
        core.statistic().profiling().addTask("game_download", hash);
        try {
            downloadLog.generateResponseLog(false, filePath);

            FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                @Override
                public void finish(DownloadFileState fileState) {
                    if (fileState != null && fileState.file != null) {
                        if (fileState.downloadedSize == fileState.totalSize) {
                            if (!fileChecker.checkWithShaAndSize(
                                    fileState.file,
                                    archiveSize,
                                    archiveSha1,
                                    true
                            )) {
                                useCaseCallback.onError("File sha or size is incorrect");
                            } else {
                                useCaseCallback.onSuccess(fileState.file);
                            }
                            core.statistic().profiling().setReady(hash);
                        }
                        CacheJournalItem cacheJournalItem = generateCacheItem();
                        cacheJournalItem.setDownloadedSize(fileState.downloadedSize);
                        cacheJournalItem.setSize(fileState.totalSize);
                        try {
                            getCache().put(cacheJournalItem, type);
                        } catch (IOException ignored) {

                        }
                    } else {
                        useCaseCallback.onError("File downloading was interrupted");
                    }
                }
            };
            core
                    .contentLoader()
                    .downloader()
                    .downloadFile(
                            url,
                            new File(filePath),
                            new FileLoadProgressCallback() {
                                @Override
                                public void onProgress(long loadedSize, long totalSize) {
                                    progressCallback.onProgress(loadedSize, totalSize);
                                }

                                @Override
                                public void onSuccess(File file) {

                                }

                                @Override
                                public void onError(String error) {
                                    useCaseCallback.onError(error);
                                }
                            },
                            downloadLog.responseLog,
                            interruption,
                            offset,
                            -1,
                            callback
                    );
        } catch (Exception e) {
            useCaseCallback.onError(e.getMessage());
        }
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                type,
                archiveSha1,
                null,
                System.currentTimeMillis(),
                0,
                0,
                null
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return core.contentLoader().getInfiniteCache();
    }
}
