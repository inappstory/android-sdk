package com.inappstory.sdk.stories.cache.usecases;


import androidx.annotation.WorkerThread;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;

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
        final DownloadFileState[] fileState = {getCache().get(uniqueKey)};
        if (fileState[0] == null || fileState[0].downloadedSize != fileState[0].totalSize) {
            try {
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState state) {
                        downloadLog.sendResponseLog();
                        if (state == null || state.downloadedSize != state.totalSize) {
                            return;
                        }
                        CacheJournalItem cacheJournalItem = generateCacheItem();
                        cacheJournalItem.setSize(state.totalSize);
                        cacheJournalItem.setDownloadedSize(state.totalSize);
                        try {
                            getCache().put(cacheJournalItem);
                        } catch (IOException e) {
                        }
                        fileState[0] = state;
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
                e.printStackTrace();
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
        return filesDownloadManager.getCachesHolder().getCommonCache();
    }

}
