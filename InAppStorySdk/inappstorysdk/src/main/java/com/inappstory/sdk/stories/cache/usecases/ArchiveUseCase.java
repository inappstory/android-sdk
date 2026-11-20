package com.inappstory.sdk.stories.cache.usecases;

import static java.util.UUID.randomUUID;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.GameNameHolder;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerError;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

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
    private final FileChecker fileChecker;
    private final boolean useLocalFile;

    public ArchiveUseCase(
            IASCore core,
            String url,
            FileChecker fileChecker,
            long archiveSize,
            String archiveSha1,
            long totalFilesSize,
            ProgressCallback progressCallback,
            DownloadInterruption interruption,
            UseCaseCallback<File> useCaseCallback,
            boolean useLocalFile
    ) {
        super(core);
        this.fileChecker = fileChecker;
        this.url = url;
        this.totalFilesSize = totalFilesSize;
        this.uniqueKey = StringsUtils.md5(url);
        this.archiveName = getArchiveName(url);
        this.archiveSize = archiveSize;
        this.archiveSha1 = archiveSha1;
        this.progressCallback = progressCallback;
        this.interruption = interruption;
        this.useCaseCallback = useCaseCallback;
        this.useLocalFile = useLocalFile;
        File cacheDir = getCache().getCacheDir();
        String fileName = this.uniqueKey + StringsUtils.getExtensionFromUrl(url);
        String subPath = "v2" +
                File.separator +
                "zip" +
                File.separator +
                this.archiveName +
                File.separator +
                fileName;
        this.filePath = new File(cacheDir, subPath).getAbsolutePath();
    }

    private String getArchiveName(String url) {
        return new GameNameHolder().getGameName(url);
    }

    @Override
    @WorkerThread
    public Void getFile() {
        if (!useLocalFile) {
            removeOldVersions(true);
            try {
                getCache().delete(generateCacheItem(0, 0), type);
            } catch (Exception e) {
                InAppStoryManager.showDLog(
                        LoggerTags.IAS_WARN_TAG,
                        "Failed to delete old version. Error: " + e.getMessage()
                );
            }
            downloadLog.generateRequestLog(url);
            downloadArchive();
        } else if (!useLocalArchive()) {
            downloadArchive();
        }
        return null;
    }

    private boolean useLocalArchive() {
        LruDiskCache cache = getCache();
        File cachedArchive = cache.getFullFile(uniqueKey, type);

        downloadLog.generateRequestLog(url);
        if (cachedArchive != null) {
            if (fileChecker.checkWithShaAndSize(
                    cachedArchive,
                    archiveSize,
                    archiveSha1,
                    true
            ) instanceof FileCheckerError) {
                try {
                    cache.deleteArchiveAndExtractedFolder(uniqueKey, type);
                } catch (Exception e) {
                    InAppStoryManager.showDLog(
                            LoggerTags.IAS_WARN_TAG,
                            "Failed to delete invalid archive directory. Error: " + e.getMessage()
                    );
                }
                useCaseCallback.onError(new SimpleUseCaseError("Invalid archive deleted"));
                return false;
            } else {
                useCaseCallback.onSuccess(cachedArchive);
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                return true;
            }
        } else {
            removeOldVersions(false);
        }
        return false;
    }

    private void removeOldVersions(boolean inclusive) {
        try {
            getCache().clearOldArchiveVersions(archiveName, inclusive);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to clear old archive versions. Error: " + e.getMessage()
            );
        }
    }

    private void downloadArchive() {
        downloadLog.sendRequestLog();

        if (!validateDownloadPreconditions()) {
            return;
        }

        final String hash = randomUUID().toString();
        long offset = getDownloadOffset();

        core.statistic().profiling().addTask("game_download", hash);
        downloadLog.generateResponseLog(false, filePath);

        try {
            initiateDownload(hash, offset);
        } catch (Exception e) {
            useCaseCallback.onError(new SimpleUseCaseError(e.getMessage()));
        }
    }

    private boolean validateDownloadPreconditions() {
        File cacheDir = getCache().getCacheDir();
        if (!filePath.startsWith(
                cacheDir.getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip"
        )) {
            useCaseCallback.onError(new SimpleUseCaseError("Error in game name"));
            return false;
        }

        if (totalFilesSize > cacheDir.getFreeSpace()) {
            useCaseCallback.onError(new SimpleUseCaseError("No free space for download"));
            return false;
        }

        return true;
    }

    private long getDownloadOffset() {
        DownloadFileState fileState = getCache().get(uniqueKey);
        return (fileState != null) ? fileState.downloadedSize : 0;
    }

    private void initiateDownload(String hash, long offset) {
        FinishDownloadFileCallback finishCallback = createFinishCallback(hash);
        FileLoadProgressCallback progressCallback = createProgressCallback();

        core.filesDownloader().downloadFile(
                url,
                new File(filePath),
                progressCallback,
                downloadLog.responseLog,
                interruption,
                offset,
                finishCallback
        );
    }

    private FinishDownloadFileCallback createFinishCallback(final String hash) {
        return new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState state) {
                if (state == null || state.file == null) {
                    useCaseCallback.onError(new SimpleUseCaseError("File downloading was interrupted"));
                    return;
                }

                if (state.isFullDownload()) {
                    handleFullDownloadCompletion(state, hash);
                }

                updateCacheJournal(state);
            }

            @Override
            public void waiting() {
            }
        };
    }

    private void handleFullDownloadCompletion(DownloadFileState state, String hash) {
        FileCheckerResult checkerResult = fileChecker.checkWithShaAndSize(
                state.file,
                archiveSize,
                archiveSha1,
                true
        );

        if (checkerResult instanceof FileCheckerError) {
            useCaseCallback.onError(new SimpleUseCaseError(
                    "Game archive sha or size is incorrect: " + checkerResult
            ));
        } else {
            useCaseCallback.onSuccess(state.file);
        }

        core.statistic().profiling().setReady(hash);
        downloadLog.sendResponseLog();
    }

    private void updateCacheJournal(DownloadFileState state) {
        long maxSize = Math.max(state.totalSize, state.downloadedSize);
        CacheJournalItem cacheJournalItem = generateCacheItem(maxSize, state.downloadedSize);

        try {
            getCache().put(cacheJournalItem, type);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal after download. Error: " + e.getMessage()
            );
        }
    }

    private FileLoadProgressCallback createProgressCallback() {
        return new FileLoadProgressCallback() {
            @Override
            public void onProgress(long loadedSize, long totalSize) {
                progressCallback.onProgress(loadedSize, totalSize);
            }

            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onError(String error) {
                useCaseCallback.onError(new SimpleUseCaseError(error));
            }
        };
    }

    @Override
    protected String journalItemType() {
        return type;
    }

    @Override
    protected String journalItemSha1() {
        return archiveSha1;
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
