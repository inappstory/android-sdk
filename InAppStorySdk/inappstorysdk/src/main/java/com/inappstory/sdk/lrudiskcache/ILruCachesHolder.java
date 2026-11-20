package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;

public interface ILruCachesHolder {
    LruDiskCache getCommonCache();

    LruDiskCache getFastCache();

    LruDiskCache getVodCache();

    LruDiskCache getBundleCache();

    LruDiskCache getInfiniteCache();

    VODCacheJournal getVodCacheJournal();

    void setCacheSizes(long freeSpace);

    void clear();
}
