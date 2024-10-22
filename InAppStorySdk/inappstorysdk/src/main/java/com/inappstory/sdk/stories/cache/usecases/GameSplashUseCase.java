package com.inappstory.sdk.stories.cache.usecases;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.interfaces.IDownloadResource;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;

public class GameSplashUseCase extends GetCacheFileUseCase<DownloadFileState> {
    private final FileChecker fileChecker = new FileChecker();
    private final IDownloadResource splashScreen;

    public GameSplashUseCase(
            FilesDownloadManager filesDownloadManager,
            IDownloadResource splashScreen
    ) {
        super(filesDownloadManager);
        this.splashScreen = splashScreen;
        this.uniqueKey = StringsUtils.md5(splashScreen.url());
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "games" +
                File.separator +
                "splashes" +
                File.separator +
                uniqueKey +
                Downloader.getFileExtensionFromUrl(splashScreen.url());
    }

    @WorkerThread
    @Override
    public DownloadFileState getFile() {
        downloadLog.generateRequestLog(splashScreen.url());
        final DownloadFileState[] fileState = {getCache().get(uniqueKey)};
        if (fileState[0] == null || fileState[0].downloadedSize != fileState[0].totalSize) {
            try {
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                FinishDownloadFileCallback callback =
                        new FinishDownloadFileCallback() {
                            @Override
                            public void finish(DownloadFileState state) {
                                downloadLog.sendResponseLog();
                                if (state == null || state.downloadedSize != state.totalSize) {
                                    fileState[0] = null;
                                    return;
                                }
                                if (fileChecker.checkWithShaAndSize(
                                        state.file,
                                        splashScreen.size(),
                                        splashScreen.sha1(),
                                        true
                                )) {
                                    CacheJournalItem cacheJournalItem = generateCacheItem();
                                    cacheJournalItem.setSize(state.totalSize);
                                    cacheJournalItem.setDownloadedSize(state.totalSize);
                                    try {
                                        getCache().put(cacheJournalItem);
                                    } catch (IOException e) {

                                    }
                                    fileState[0] =state;
                                }
                            }
                        };
                if (filesDownloadManager.addFinishCallback(splashScreen.url(), callback))
                    Downloader.downloadFile(
                            splashScreen.url(),
                            new File(filePath),
                            null,
                            downloadLog.responseLog,
                            null,
                            filesDownloadManager
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
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                null,
                null,
                null,
                System.currentTimeMillis(),
                0,
                0,
                null
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
