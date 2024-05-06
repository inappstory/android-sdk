package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class StoryResourceFileUseCase extends GetCacheFileUseCase<DownloadFileState> {
    private final String url;

    public StoryResourceFileUseCase(
            FilesDownloadManager filesDownloadManager,
            String url
    ) {
        super(filesDownloadManager);
        this.url = url;
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "stories" +
                File.separator +
                "resources" +
                File.separator +
                uniqueKey +
                Downloader.getFileExtensionFromUrl(url);
    }

    @WorkerThread
    @Override
    public DownloadFileState getFile() {
        downloadLog.generateRequestLog(url);
        Log.e("ScenarioDownload", "UniqueKey: " + uniqueKey);
        DownloadFileState fileState = getCache().get(uniqueKey);
        if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
            try {
                Log.e("ScenarioDownload", "Download: " + uniqueKey);
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                fileState = Downloader.downloadFile(
                        url,
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
                Log.e("ScenarioDownload", "Downloaded: " + uniqueKey);
                CacheJournalItem cacheJournalItem = generateCacheItem();
                cacheJournalItem.setSize(fileState.totalSize);
                cacheJournalItem.setDownloadedSize(fileState.totalSize);
                getCache().put(cacheJournalItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ScenarioDownload", "Cached: " + uniqueKey);
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
        return filesDownloadManager.getCachesHolder().getCommonCache();
    }

}
