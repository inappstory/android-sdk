package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IASContentLoaderImpl implements IASContentLoader {
    private final IASCore core;
    private final FilesDownloadManager filesDownloadManager;
    private final GameCacheManager gameCacheManager;
    private final FilesDownloader filesDownloader;
    private final InAppMessageDownloadManager inAppMessageDownloadManager;

    private final StoryDownloadManager storyDownloadManager;

    public IASContentLoaderImpl(IASCore core) {
        this.core = core;
        this.filesDownloader = new FilesDownloader(core);
        this.filesDownloadManager = new FilesDownloadManager(core);
        this.storyDownloadManager = new StoryDownloadManager(core);
        this.gameCacheManager = new GameCacheManager(core);
        this.inAppMessageDownloadManager = new InAppMessageDownloadManager(core);
        runFreeSpaceCheck();
    }

    @Override
    public FilesDownloadManager filesDownloadManager() {
        return filesDownloadManager;
    }

    @Override
    public LruDiskCache getFastCache() {
        return filesDownloadManager.getCachesHolder().getFastCache();
    }

    @Override
    public LruDiskCache getCommonCache() {
        return filesDownloadManager.getCachesHolder().getCommonCache();
    }

    @Override
    public LruDiskCache getVodCache() {
        return filesDownloadManager.getCachesHolder().getVodCache();
    }

    @Override
    public LruDiskCache getInfiniteCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }

    @Override
    public LruDiskCache getBundleCache() {
        return filesDownloadManager.getCachesHolder().getBundleCache();
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
    public GameCacheManager gameCacheManager() {
        return gameCacheManager;
    }

    @Override
    public void clearCache() {
        try {
            core.storiesListVMHolder().clear();
            getCommonCache().clearCache();
            getFastCache().clearCache();
            getInfiniteCache().clearCache();
            getBundleCache().clearCache();
            getVodCache().clearCache();
            filesDownloadManager().getVodCacheJournal().clear();
            core.contentHolder().clearAll();
            storyDownloadManager.clearCache();
            core.keyValueStorage().clear();
        } catch (IOException ignored) {

        }
        core.sessionManager().getSession().assetsIsCleared();
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
        getFastCache().setCacheSize(fastCacheType);
        getCommonCache().setCacheSize(cacheType);
    }

    @Override
    public void addVODResources(IReaderContent readerContent, int slideIndex) {
        List<IResource> resources = new ArrayList<>();
        resources.addAll(readerContent.vodResources(slideIndex));
        for (IResource object : resources) {
            if (filesDownloadManager.getVodCacheJournal().getItem(object.getFileName()) == null) {
                filesDownloadManager.getVodCacheJournal().putItem(new VODCacheJournalItem(
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

    private ScheduledExecutorService checkSpaceThread = new ScheduledThreadPoolExecutor(1);

    Runnable checkFreeSpace = new Runnable() {
        @Override
        public void run() {
            LruDiskCache commonCache = getCommonCache();
            LruDiskCache fastCache = getFastCache();
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
        if (checkSpaceThread.isShutdown()) {
            checkSpaceThread = new ScheduledThreadPoolExecutor(1);
        }
        checkSpaceThread.scheduleAtFixedRate(checkFreeSpace, 1L, 60000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public FilesDownloader downloader() {
        return filesDownloader;
    }
}
