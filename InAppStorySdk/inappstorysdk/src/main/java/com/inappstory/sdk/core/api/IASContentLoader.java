package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;

public interface IASContentLoader {
    FilesDownloadManager filesDownloadManager();
    LruDiskCache getFastCache();
    LruDiskCache getCommonCache();
    LruDiskCache getVodCache();
    LruDiskCache getInfiniteCache();
    StoryDownloadManager storyDownloadManager();
    GameCacheManager gameCacheManager();
    void clearCache();
    void clearGames();
    void setCacheSizes();
    void addVODResources(Story story, int slideIndex);

}
