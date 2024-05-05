package com.inappstory.sdk.stories.cache.usecases;

import static java.util.UUID.randomUUID;

import androidx.annotation.WorkerThread;

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
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class ArchiveUseCase extends GetCacheFileUseCase<Void> {
    private final String url;
    private final String instanceId;
    private final String archiveName;
    private final String archiveSha1;
    private final String type = "Archive";
    private final long totalFilesSize;
    private final long archiveSize;
    private final FileLoadProgressCallback progressCallback;
    private final DownloadInterruption interruption;
    private final UseCaseCallback<File> useCaseCallback;
    private final FileChecker fileChecker = new FileChecker();
    private int offset;


    public ArchiveUseCase(
            FilesDownloadManager filesDownloadManager,
            String url,
            String instanceId,
            long archiveSize,
            String archiveSha1,
            long totalFilesSize,
            FileLoadProgressCallback progressCallback,
            DownloadInterruption interruption,
            UseCaseCallback<File> useCaseCallback
    ) {
        super(filesDownloadManager);
        this.url = url;
        this.totalFilesSize = totalFilesSize;
        this.uniqueKey = StringsUtils.md5(url);
        this.instanceId = instanceId;
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
                "." +
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
                    cache.delete(url);
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
        }
        return false;
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
        String hash = randomUUID().toString();
        File zipFile = new File(filePath);
        DownloadFileState fileState = null;
        ProfilingManager.getInstance().addTask("game_download", hash);
        try {
            fileState = Downloader.downloadFile(
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
                    null,
                    0
            );
            fileState = Downloader.downloadFile(
                    url,
                    true,
                    getCache(),
                    zipFile,
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
                    interruption,
                    hash
            );
        } catch (Exception e) {
            useCaseCallback.onError(e.getMessage());
        }
        if (fileState != null && fileState.file != null &&
                (fileState.downloadedSize == fileState.totalSize)) {
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
            ProfilingManager.getInstance().setReady(hash);
        } else {
            useCaseCallback.onError("File downloading was interrupted");
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
                archiveSize,
                archiveSize
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
