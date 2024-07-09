package com.inappstory.sdk.packages.core.base.lrucache;

public interface ILruCache<K, I> {
    void put(K key, I item);
    I get(K key);
    void clear();
}
