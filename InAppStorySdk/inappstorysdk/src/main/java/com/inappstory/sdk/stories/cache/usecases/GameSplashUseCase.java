package com.inappstory.sdk.stories.cache.usecases;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.FileCheckerSuccess;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IDownloadResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class GameSplashUseCase extends GetCacheFileUseCase<DownloadFileState> {
    private final FileChecker fileChecker;
    private final IDownloadResource splashScreen;

    public GameSplashUseCase(
            IASCore core,
            FileChecker fileChecker,
            IDownloadResource splashScreen
    ) {
        super(core);
        this.fileChecker = fileChecker;
        this.splashScreen = splashScreen;
        this.uniqueKey = StringsUtils.md5(splashScreen.url());
        this.filePath = new FilePathCacheGenerator(
                uniqueKey +
                        StringsUtils.getExtensionFromUrl(splashScreen.url()),
                core,
                FilePathCacheType.GAME_SPLASH
        ).generate();
    }

    @WorkerThread
    @Override
    public DownloadFileState getFile() {
        downloadLog.generateRequestLog(splashScreen.url());
        DownloadFileState cachedState = getCache().get(uniqueKey);

        if (cachedState != null && cachedState.isFullDownload()) {
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            return cachedState;
        }

        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);

        return performSynchronousDownload();
    }

    private DownloadFileState performSynchronousDownload() {
        // Array is used to capture the result from the synchronous void callback
        final DownloadFileState[] downloadResult = new DownloadFileState[1];

        try {
            core.filesDownloader().downloadFile(
                    splashScreen.url(),
                    new File(filePath),
                    null,
                    downloadLog.responseLog,
                    null,
                    createFinishCallback(downloadResult)
            );
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to initiate splash screen download. Error: " + e.getMessage()
            );
        }

        return downloadResult[0];
    }

    private FinishDownloadFileCallback createFinishCallback(final DownloadFileState[] downloadResult) {
        return new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState state) {
                downloadLog.sendResponseLog();

                if (state != null && state.isFullDownload()) {
                    if (isValidSplashScreen(state)) {
                        updateCacheJournal(state);
                        // Mutate the array to pass the result back to the synchronous caller
                        downloadResult[0] = state;
                    }
                }
            }

            @Override
            public void waiting() {
            }
        };
    }

    private boolean isValidSplashScreen(DownloadFileState state) {
        FileCheckerResult checkerResult = fileChecker.checkWithShaAndSize(
                state.file,
                splashScreen.size(),
                splashScreen.sha1(),
                true
        );
        return checkerResult instanceof FileCheckerSuccess;
    }

    private void updateCacheJournal(DownloadFileState state) {
        long maxSize = Math.max(state.totalSize, state.downloadedSize);
        CacheJournalItem cacheJournalItem = generateCacheItem(maxSize, state.downloadedSize);

        try {
            getCache().put(cacheJournalItem);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal for splash screen. Error: " + e.getMessage()
            );
        }
    }


    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
