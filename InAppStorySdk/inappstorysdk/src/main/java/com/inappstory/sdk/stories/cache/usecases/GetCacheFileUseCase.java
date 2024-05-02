package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.lrudiskcache.LruCachesHolder;


public abstract class GetCacheFileUseCase {
    protected LruCachesHolder cachesHolder;

    public GetCacheFileUseCase(LruCachesHolder cachesHolder) {
        this.cachesHolder = cachesHolder;
    }

    abstract void getFile();
}
