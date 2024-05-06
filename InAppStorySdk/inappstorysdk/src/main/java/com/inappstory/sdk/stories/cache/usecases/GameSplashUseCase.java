package com.inappstory.sdk.stories.cache.usecases;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class GameSplashUseCase extends GetCacheFileUseCase<DownloadFileState> {
    private final FileChecker fileChecker = new FileChecker();
    private final GameSplashScreen splashScreen;

    public GameSplashUseCase(
            FilesDownloadManager filesDownloadManager,
            GameSplashScreen splashScreen
    ) {
        super(filesDownloadManager);
        this.splashScreen = splashScreen;
        this.uniqueKey = StringsUtils.md5(splashScreen.url);
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "games" +
                File.separator +
                "splashes" +
                File.separator +
                uniqueKey +
                Downloader.getFileExtensionFromUrl(splashScreen.url);
    }

    @WorkerThread
    @Override
    public DownloadFileState getFile() {
        downloadLog.generateRequestLog(splashScreen.url);
        DownloadFileState fileState = getCache().get(uniqueKey);
        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
            try {
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                fileState = Downloader.downloadFile(
                        splashScreen.url,
                        new File(filePath),
                        null,
                        downloadLog.responseLog,
                        null,
                        0
                );
                downloadLog.sendResponseLog();
                if (fileState == null || fileState.downloadedSize != fileState.totalSize)  {
                    return null;
                }
                if (fileChecker.checkWithShaAndSize(
                        fileState.file,
                        splashScreen.size,
                        splashScreen.sha1,
                        true
                )) {
                    CacheJournalItem cacheJournalItem = generateCacheItem();
                    cacheJournalItem.setSize(fileState.totalSize);
                    cacheJournalItem.setDownloadedSize(fileState.totalSize);
                    getCache().put(cacheJournalItem);
                    return fileState;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
        }
        return fileState;
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
                0
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
