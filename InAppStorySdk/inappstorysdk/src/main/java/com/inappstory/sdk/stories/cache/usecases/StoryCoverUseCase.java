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
import java.io.IOException;

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
                Downloader.getFileExtensionFromUrl(url);
    }

    @Override
    public Void getFile() {
        downloadLog.generateRequestLog(url);
        Log.e("ScenarioDownload", "UniqueKey: " + uniqueKey);
        filesDownloadManager.useLocalFilesThread(new Runnable() {
            @Override
            public void run() {
                final LruDiskCache cache = getCache();
                DownloadFileState fileState = cache.get(uniqueKey);
                if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                    Log.e("ScenarioDownload", "Download: " + uniqueKey + " Url: " + url);
                    downloadLog.sendRequestLog();
                    downloadLog.generateResponseLog(false, filePath);
                    filesDownloadManager.useFastDownloader(new Runnable() {
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
                                            cache.put(cacheJournalItem);
                                        } catch (IOException e) {

                                            getStoryCoverCallback.error();
                                        }
                                        getStoryCoverCallback.success(filePath);
                                    }
                                };
                                Downloader.downloadFile(
                                        url,
                                        new File(filePath),
                                        null,
                                        downloadLog.responseLog,
                                        null,
                                        filesDownloadManager,
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
            }
        });

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
        return filesDownloadManager.getCachesHolder().getFastCache();
    }
}
