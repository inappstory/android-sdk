package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.banners.BannerDownloadManager;
import com.inappstory.sdk.core.inappmessages.InAppMessageDownloadManager;
import com.inappstory.sdk.game.cache.GameCacheManager;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.cache.DownloadThreadsHolder;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;

public interface IASContentLoader {
    DownloadThreadsHolder downloadThreads();
    StoryDownloadManager storyDownloadManager();
    InAppMessageDownloadManager inAppMessageDownloadManager();
    BannerDownloadManager bannerDownloadManager();
    GameCacheManager gameCacheManager();

    boolean getIamWereLoadedStatus(String tagsHash);
    void changeIamWereLoadedStatus(String tagsHash);
    void clearIamWereLoadedStatuses();

    void clear();
    void clearGames();
    void addVODResources(IReaderContent readerContent, int slideIndex);
}
