package com.inappstory.sdk.stories.cache.usecases;


import android.util.Pair;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.cache.vod.ContentRange;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.stories.cache.vod.VODDownloader;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.ArrayList;

public class StoryVODResourceFileUseCase extends GetCacheFileUseCase<StoryVODResourceFileUseCaseResult> {
    private final String url;
    private final long rangeStart;
    private final long rangeEnd;


    public StoryVODResourceFileUseCase(
            IASCore core,
            String url,
            String uniqueKey,
            long rangeStart,
            long rangeEnd
    ) {
        super(core);
        this.url = url;
        this.uniqueKey = uniqueKey;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.filePath = new FilePathCacheGenerator(
                url,
                core,
                FilePathCacheType.STORY_VOD_RESOURCE
        ).generate();
    }

    @WorkerThread
    @Override
    public StoryVODResourceFileUseCaseResult getFile() {
        downloadLog.generateRequestLog(url);
        DownloadFileState fileState = getCache().get(uniqueKey);
        VODCacheJournal vodCacheJournal = core.contentLoader().filesDownloadManager()
                .getVodCacheJournal();
        VODCacheJournalItem vodJournalItem = vodCacheJournal.getItem(uniqueKey);
        VODDownloader vodDownloader = new VODDownloader(core);
        Pair<ContentRange, byte[]> result = null;
        if (fileState != null
                && fileState.file != null
                && fileState.file.exists()
                && vodJournalItem != null
                && vodJournalItem.hasPart(rangeStart, rangeEnd)
        ) {
            result = vodDownloader.getBytesFromFile(
                    new ContentRange(
                            rangeStart,
                            rangeEnd,
                            vodJournalItem.getFullSize()
                    ),
                    fileState.file.getAbsolutePath()
            );
            if (result != null) {
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                return new StoryVODResourceFileUseCaseResult(
                        result.first,
                        fileState.file,
                        true
                );
            }
        }
        if (vodJournalItem == null) {
            vodJournalItem = generateVODCacheItem();
            vodCacheJournal.putItem(vodJournalItem);
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
                if (vodDownloader.putBytesToFile(rangeStart, result.second, filePath)) {
                    vodJournalItem.addPart(rangeStart, rangeEnd);
                    vodJournalItem.setFullSize(result.first.length());
                    vodCacheJournal.putItem(vodJournalItem);
                    long size = vodJournalItem.getDownloadedSize();

                    CacheJournalItem cacheJournalItem = generateCacheItem();
                    cacheJournalItem.setSize(size);
                    cacheJournalItem.setDownloadedSize(size);
                    getCache().put(cacheJournalItem);
                    return new StoryVODResourceFileUseCaseResult(
                            result.first,
                            new File(filePath),
                            false);
                }
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
        return core.contentLoader().getVodCache();
    }

}
