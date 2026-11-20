package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.lrudiskcache.LruDiskCache.MB_10;
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
import com.inappstory.sdk.stories.cache.DownloadThreadsHolder;
import com.inappstory.sdk.stories.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.cache.vod.VODCacheItemPart;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournalItem;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void clear() {
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
}
