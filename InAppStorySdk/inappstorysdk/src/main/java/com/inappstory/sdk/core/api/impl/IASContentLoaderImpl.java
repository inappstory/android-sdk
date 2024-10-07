package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_1;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_100;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_5;
import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_50;

import android.content.Context;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentLoader;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.IOException;

public class IASContentLoaderImpl implements IASContentLoader {
    private final IASCore core;
    private final FilesDownloadManager filesDownloadManager;
    private final GameCacheManager gameCacheManager;

    private final StoryDownloadManager storyDownloadManager;

    public IASContentLoaderImpl(IASCore core) {
        this.core = core;
        this.filesDownloadManager = new FilesDownloadManager(core);
        this.storyDownloadManager = new StoryDownloadManager(core);
        this.gameCacheManager = new GameCacheManager(core);
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
    public StoryDownloadManager storyDownloadManager() {
        return storyDownloadManager;
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
            getVodCache().clearCache();
            filesDownloadManager().getVodCacheJournal().clear();
            storyDownloadManager.clearCache();
            KeyValueStorage.clear();
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
}
