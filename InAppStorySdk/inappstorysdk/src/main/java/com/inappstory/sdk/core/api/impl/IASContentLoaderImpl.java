package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.core.banners.BannerDownloadManager;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.DownloadThreadsHolder;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class IASContentLoaderImpl implements IASContentLoader {
    private final IASCore core;
    private final DownloadThreadsHolder downloadThreadsHolder;
    private final GameCacheManager gameCacheManager;
    private final InAppMessageDownloadManager inAppMessageDownloadManager;
    private final BannerDownloadManager bannerDownloadManager;
    private Set<String> iamByTagsWereLoaded = new HashSet<>();

    private final StoryDownloadManager storyDownloadManager;

    public IASContentLoaderImpl(IASCore core) {
        this.core = core;
        this.downloadThreadsHolder = new DownloadThreadsHolder();
        this.storyDownloadManager = new StoryDownloadManager(core);
        this.gameCacheManager = new GameCacheManager(core);
        this.inAppMessageDownloadManager = new InAppMessageDownloadManager(core);
        this.bannerDownloadManager = new BannerDownloadManager(core);
        runFreeSpaceCheck();
    }

    @Override
    public DownloadThreadsHolder downloadThreads() {
        return downloadThreadsHolder;
    }

    @Override
    public StoryDownloadManager storyDownloadManager() {
        return storyDownloadManager;
    }

    @Override
    public InAppMessageDownloadManager inAppMessageDownloadManager() {
        return inAppMessageDownloadManager;
    }

    @Override
    public BannerDownloadManager bannerDownloadManager() {
        return bannerDownloadManager;
    }

    @Override
    public GameCacheManager gameCacheManager() {
        return gameCacheManager;
    }

    @Override
    public boolean getIamWereLoadedStatus(String tagsHash) {
        synchronized (this) {
            return iamByTagsWereLoaded.contains(tagsHash);
        }
    }

    @Override
    public void changeIamWereLoadedStatus(String tagsHash) {
        synchronized (this) {
            iamByTagsWereLoaded.add(tagsHash);
        }
    }

    @Override
    public void clearIamWereLoadedStatuses() {
        synchronized (this) {
            iamByTagsWereLoaded.clear();
        }
    }

    @Override
    public void clearCache() {
        try {
            inAppMessageDownloadManager.clearSlidesDownloader();
            inAppMessageDownloadManager.clearLocalData();
            bannerDownloadManager.clearSlidesDownloader();
            bannerDownloadManager.clearLocalData();
            storyDownloadManager.clearCache();
            synchronized (this) {
                iamByTagsWereLoaded.clear();
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void clearGames() {
        gameCacheManager.clearGames();
    }

    @Override
    public void setCacheSizes() {
        long cacheType = MB_100;
        long fastCacheType = MB_10;
        long freeSpace = core.appContext().getCacheDir().getFreeSpace();
        if (freeSpace < cacheType + fastCacheType + MB_10) {
            cacheType = MB_50;
            if (freeSpace < cacheType + fastCacheType + MB_10) {
                cacheType = MB_10;
                fastCacheType = MB_5;
                if (freeSpace < cacheType + fastCacheType + MB_10) {
                    cacheType = MB_1;
                    fastCacheType = MB_1;
                }
            }
        }
        core.cachesHolder().getFastCache().setCacheSize(fastCacheType);
        core.cachesHolder().getCommonCache().setCacheSize(cacheType);
    }

    @Override
    public void addVODResources(IReaderContent readerContent, int slideIndex) {
        List<IResource> resources = new ArrayList<>();
        resources.addAll(readerContent.vodResources(slideIndex));
        VODCacheJournal journal = core.cachesHolder().getVodCacheJournal();
        for (IResource object : resources) {
            if (journal.getItem(object.getFileName()) == null) {
                journal.putItem(new VODCacheJournalItem(
                        "",
                        object.getFileName(),
                        "",
                        "",
                        new ArrayList<VODCacheItemPart>(),
                        "",
                        0,
                        object.getUrl(),
                        System.currentTimeMillis()
                ));
            }
        }
    }

    private ScheduledTPEManager checkSpaceThread = new ScheduledTPEManager();

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            LruDiskCache commonCache = core.cachesHolder().getCommonCache();
            LruDiskCache fastCache = core.cachesHolder().getFastCache();
            if (commonCache != null && fastCache != null) {
                long freeSpace = commonCache.getCacheDir().getFreeSpace();
                if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                    commonCache.setCacheSize(MB_50);
                    if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                        commonCache.setCacheSize(MB_10);
                        fastCache.setCacheSize(MB_5);
                        if (freeSpace < commonCache.getCacheSize() + fastCache.getCacheSize() + MB_10) {
                            commonCache.setCacheSize(MB_10);
                            fastCache.setCacheSize(MB_5);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void runFreeSpaceCheck() {
        checkSpaceThread.scheduleAtFixedRate(checkFreeSpace, 1L, 60000L, TimeUnit.MILLISECONDS);
    }
}
