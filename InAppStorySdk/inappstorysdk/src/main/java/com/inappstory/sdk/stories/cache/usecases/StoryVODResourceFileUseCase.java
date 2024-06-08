package com.inappstory.sdk.stories.cache.usecases;


import android.util.Pair;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.vod.ContentRange;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.stories.cache.vod.VODDownloader;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.ArrayList;

public class StoryVODResourceFileUseCase extends GetCacheFileUseCase<StoryVODResourceFileUseCaseResult> {
    private final String url;
    private final long rangeStart;
    private final long rangeEnd;

    public StoryVODResourceFileUseCase(
            FilesDownloadManager filesDownloadManager,
            String url,
            String uniqueKey,
            long rangeStart,
            long rangeEnd
    ) {
        super(filesDownloadManager);
        this.url = url;
        this.uniqueKey = uniqueKey;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "stories" +
                File.separator +
                "resources" +
                File.separator +
                StringsUtils.md5(url) +
                Downloader.getFileExtensionFromUrl(url);
    }

    @WorkerThread
    @Override
    public StoryVODResourceFileUseCaseResult getFile() {
        downloadLog.generateRequestLog(url);
        DownloadFileState fileState = getCache().get(uniqueKey);
        VODCacheJournalItem vodJournalItem = filesDownloadManager.vodCacheJournal.getItem(uniqueKey);
        VODDownloader vodDownloader = new VODDownloader();
        Pair<ContentRange, byte[]> result = null;
        if (fileState != null
                && fileState.file != null
                && fileState.file.exists()
                && vodJournalItem != null
        ) {
            result = vodDownloader.getBytesFromFile(
                    new ContentRange(
                            rangeStart,
                            rangeEnd,
                            vodJournalItem.getFullSize()
                    ),
                    fileState.file.getAbsolutePath()
            );

            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            return new StoryVODResourceFileUseCaseResult(
                    result.first,
                    result.second,
                    true
            );
        }
        if (vodJournalItem == null) {
            vodJournalItem = generateVODCacheItem();
            filesDownloadManager.vodCacheJournal.putItem(vodJournalItem);
        }
        try {
            downloadLog.sendRequestLog();
            downloadLog.generateResponseLog(false, filePath);
            result =
                    vodDownloader.downloadBytes(
                            url,
                            rangeStart,
                            rangeEnd,
                            getCache().getCacheDir().getFreeSpace()
                    );
            downloadLog.sendResponseLog();
            if (result != null) {
                boolean putToFile = vodDownloader.putBytesToFile(rangeStart, result.second, filePath);
                if (putToFile) {
                    vodJournalItem.addPart(rangeStart, rangeEnd);
                    vodJournalItem.setFullSize(result.first.length());
                    filesDownloadManager.vodCacheJournal.writeJournal();
                    long size = vodJournalItem.getDownloadedSize();

                    CacheJournalItem cacheJournalItem = generateCacheItem();
                    cacheJournalItem.setSize(size);
                    cacheJournalItem.setDownloadedSize(size);
                }
                return new StoryVODResourceFileUseCaseResult(
                        result.first,
                        result.second,
                        false);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private VODCacheJournalItem generateVODCacheItem() {
        return new VODCacheJournalItem(
                null,
                uniqueKey,
                null,
                null,
                new ArrayList<VODCacheItemPart>(),
                null,
                0,
                url,
                System.currentTimeMillis()
        );
    }


    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getVodCache();
    }

}
