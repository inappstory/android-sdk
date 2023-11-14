package com.inappstory.sdk.stories.filedownloader;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;

public interface ILruCacheHolder {
    @NonNull LruDiskCache getCache();
}
