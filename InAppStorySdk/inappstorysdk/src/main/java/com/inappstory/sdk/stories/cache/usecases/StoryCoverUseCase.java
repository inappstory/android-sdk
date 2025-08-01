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
        if (filesDownloadManager == null) return;
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
        if (filesDownloadManager == null) return null;
        filesDownloadManager.useLocalFilesThread(new Runnable() {
            @Override
            public void run() {
                final LruDiskCache cache = getCache();
                DownloadFileState fileState = cache.get(uniqueKey);
                if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
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
                                if (filesDownloadManager.addFinishCallback(url, callback))
                                    Downloader.downloadFile(
                                            url,
                                            new File(filePath),
                                            null,
                                            downloadLog.responseLog,
                                            null,
                                            filesDownloadManager
                                    );

                            } catch (Exception e) {
                                getStoryCoverCallback.error();
                            }
                        }
                    });
                } else {
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
        if (filesDownloadManager == null) return null;
        return filesDownloadManager.getCachesHolder().getFastCache();
    }
}
