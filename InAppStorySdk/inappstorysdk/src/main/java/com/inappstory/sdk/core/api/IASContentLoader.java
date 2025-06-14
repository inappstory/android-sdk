package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.cache.FilesDownloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;

public interface IASContentLoader {
    FilesDownloadManager filesDownloadManager();
    LruDiskCache getFastCache();
    LruDiskCache getCommonCache();
    LruDiskCache getVodCache();
    LruDiskCache getInfiniteCache();
    LruDiskCache getBundleCache();
    StoryDownloadManager storyDownloadManager();
    InAppMessageDownloadManager inAppMessageDownloadManager();
    GameCacheManager gameCacheManager();


    boolean getIamWereLoadedStatus(String tagsHash);
    void changeIamWereLoadedStatus(String tagsHash);
    void clearIamWereLoadedStatuses();

    void clearCache();
    void clearGames();
    void setCacheSizes();
    void addVODResources(IReaderContent readerContent, int slideIndex);
    void runFreeSpaceCheck();
    FilesDownloader downloader();
}
