package com.inappstory.sdk.packages.core.base.lrucache;

public interface ILruCacheWithRemove<K, I> extends ILruCache<K, I> {
    boolean delete(K key);
}
