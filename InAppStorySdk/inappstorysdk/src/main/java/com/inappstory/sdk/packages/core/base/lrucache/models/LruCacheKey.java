package com.inappstory.sdk.packages.core.base.lrucache.models;

public class LruCacheKey implements ILruCacheKey {

    String key;
    String type;

    public static final String FILE_TYPE = "File";

    public LruCacheKey(String key) {
        this.key = key;
        this.type = FILE_TYPE;
    }

    public LruCacheKey(String key, String type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String itemType() {
        return type;
    }
}
