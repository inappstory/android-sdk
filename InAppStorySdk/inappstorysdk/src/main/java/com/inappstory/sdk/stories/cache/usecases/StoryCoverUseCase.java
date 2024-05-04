package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class StoryCoverUseCase extends GetCacheFileUseCase<Void> {
    IGetStoryCoverCallback getStoryCoverCallback;
    String url;

    public StoryCoverUseCase(
            FilesDownloadManager filesDownloadManager,
            String url,
            IGetStoryCoverCallback getStoryCoverCallback
    ) {
        super(filesDownloadManager);
        this.getStoryCoverCallback = getStoryCoverCallback;
        this.url = url;
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "stories" +
                File.separator +
                "covers" +
                File.separator +
                uniqueKey +
                "." +
                Downloader.getFileExtensionFromUrl(url);
    }

    @Override
    public Void getFile() {
        downloadLog.generateRequestLog(url);
        Log.e("ScenarioDownload", "UniqueKey: " + uniqueKey);
        DownloadFileState fileState = getCache().get(uniqueKey);
        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                Log.e("ScenarioDownload", "Download: " + uniqueKey);
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                filesDownloadManager.useFastDownloader(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DownloadFileState fileState = Downloader.downloadFile(
                                    url,
                                    new File(filePath),
                                    null,
                                    downloadLog.responseLog,
                                    null,
                                    0
                            );
                            downloadLog.sendResponseLog();
                            if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                                getStoryCoverCallback.error();
                                return;
                            }
                            Log.e("ScenarioDownload", "Downloaded: " + uniqueKey);
                            CacheJournalItem cacheJournalItem = generateCacheItem();
                            cacheJournalItem.setSize(fileState.totalSize);
                            cacheJournalItem.setDownloadedSize(fileState.totalSize);
                            getCache().put(cacheJournalItem);
                            getStoryCoverCallback.success(filePath);
                        } catch (Exception e) {
                            getStoryCoverCallback.error();
                        }
                    }
                });
        } else {
            Log.e("ScenarioDownload", "Cached: " + uniqueKey);
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            getStoryCoverCallback.success(filePath);
        }
        return null;
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
        return filesDownloadManager.getCachesHolder().getFastCache();
    }
}
