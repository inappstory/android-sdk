package com.inappstory.sdk.stories.filedownloader;

import com.inappstory.sdk.lrudiskcache.LruDiskCache;

public interface ILruCacheHolder {
    LruDiskCache getCache();
}
