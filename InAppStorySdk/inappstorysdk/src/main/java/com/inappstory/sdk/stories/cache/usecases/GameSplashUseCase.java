package com.inappstory.sdk.stories.cache.usecases;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
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
        final DownloadFileState[] fileState = {getCache().get(uniqueKey)};
        if (fileState[0] == null || !fileState[0].isFullDownload()) {
            try {
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                FinishDownloadFileCallback callback =
                        new FinishDownloadFileCallback() {
                            @Override
                            public void finish(DownloadFileState state) {
                                downloadLog.sendResponseLog();
                                if (state == null || !state.isFullDownload()) {
                                    fileState[0] = null;
                                    return;
                                }
                                if (fileChecker.checkWithShaAndSize(
                                        state.file,
                                        splashScreen.size(),
                                        splashScreen.sha1(),
                                        true
                                ) instanceof FileCheckerSuccess) {
                                    long maxSize = Math.max(state.totalSize, state.downloadedSize);
                                    CacheJournalItem cacheJournalItem = generateCacheItem(
                                            maxSize, state.downloadedSize
                                    );
                                    try {
                                        getCache().put(cacheJournalItem);
                                    } catch (Exception e) {

                                    }
                                    fileState[0] = state;
                                }
                            }

                            @Override
                            public void waiting() {

                            }
                        };
                core.filesDownloader()
                        .downloadFile(
                                splashScreen.url(),
                                new File(filePath),
                                null,
                                downloadLog.responseLog,
                                null,
                                callback
                        );
                return fileState[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
        }
        return fileState[0];
    }


    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
