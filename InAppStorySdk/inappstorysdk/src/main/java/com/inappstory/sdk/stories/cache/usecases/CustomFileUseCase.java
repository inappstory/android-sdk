package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;

public class CustomFileUseCase extends GetCacheFileUseCase<Void> {
    UseCaseCallback<File> getFileCallback;
    String url;

    public CustomFileUseCase(
            IASCore core,
            String url,
            UseCaseCallback<File> getFileCallback
    ) {
        super(core);
        this.getFileCallback = getFileCallback;
        this.url = url;
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = new FilePathCacheGenerator(
                url,
                core,
                FilePathCacheType.CUSTOM_FILE
        ).generate();
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
            core
                    .contentLoader()
                    .filesDownloadManager()
                    .useCustomDownloader(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                                    @Override
                                    public void finish(DownloadFileState fileState) {
                                        downloadLog.sendResponseLog();
                                        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                                            getFileCallback.onError("");
                                            return;
                                        }
                                        Log.e("ScenarioDownload", "Downloaded: " + uniqueKey + " Url: " + url);
                                        CacheJournalItem cacheJournalItem = generateCacheItem();
                                        cacheJournalItem.setSize(fileState.totalSize);
                                        cacheJournalItem.setDownloadedSize(fileState.totalSize);
                                        try {
                                            getCache().put(cacheJournalItem);
                                        } catch (IOException e) {

                                            getFileCallback.onError(e.getMessage());
                                        }
                                        getFileCallback.onSuccess(new File(filePath));
                                    }

                                    @Override
                                    public void waiting() {

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
                                getFileCallback.onError(e.getMessage());
                            }
                        }
                    });
        } else {
            Log.e("ScenarioDownload", "Cached: " + uniqueKey);
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            getFileCallback.onSuccess(new File(filePath));
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
        return core.contentLoader().getInfiniteCache();
    }
}
