package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.io.File;
import java.io.IOException;

public class StoryCoverUseCase extends GetCacheFileUseCase<Void> {
    IGetStoryCoverCallback getStoryCoverCallback;
    String url;

    public StoryCoverUseCase(
            IASCore core,
            String url,
            IGetStoryCoverCallback getStoryCoverCallback
    ) {
        super(core);
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
                FilesDownloader.getFileExtensionFromUrl(url);
    }

    @Override
    public Void getFile() {
        downloadLog.generateRequestLog(url);
        Log.e("ScenarioDownload", "UniqueKey: " + uniqueKey);
        DownloadFileState fileState = getCache().get(uniqueKey);
        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
            Log.e("ScenarioDownload", "Download: " + uniqueKey + " Url: " + url);
            downloadLog.sendRequestLog();
            downloadLog.generateResponseLog(false, filePath);
            core.contentLoader().filesDownloadManager()
                    .useFastDownloader(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                                    @Override
                                    public void finish(DownloadFileState fileState) {
                                        downloadLog.sendResponseLog();
                                        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                                            getStoryCoverCallback.error();
                                            return;
                                        }
                                        Log.e("ScenarioDownload", "Downloaded: " + uniqueKey + " Url: " + url);
                                        CacheJournalItem cacheJournalItem = generateCacheItem();
                                        cacheJournalItem.setSize(fileState.totalSize);
                                        cacheJournalItem.setDownloadedSize(fileState.totalSize);
                                        try {
                                            getCache().put(cacheJournalItem);
                                        } catch (IOException e) {

                                            getStoryCoverCallback.error();
                                        }
                                        getStoryCoverCallback.success(filePath);
                                    }
                                };
                                core
                                        .contentLoader()
                                        .downloader()
                                        .downloadFile(
                                        url,
                                        new File(filePath),
                                        null,
                                        downloadLog.responseLog,
                                        null,
                                        callback
                                );

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
                0,
                null
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return core.contentLoader().getFastCache();
    }
}
