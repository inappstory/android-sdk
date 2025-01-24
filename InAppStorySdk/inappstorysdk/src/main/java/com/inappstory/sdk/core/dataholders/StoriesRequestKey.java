package com.inappstory.sdk.core.dataholders;

import java.util.Objects;

public class StoriesRequestKey {
    private String cacheId;
    private String feed;
    private String tagsHash;
    private boolean isFavorite;

    public StoriesRequestKey(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public StoriesRequestKey(String cacheId, String feed, String tagsHash) {
        this.cacheId = cacheId;
        this.feed = feed;
        this.tagsHash = tagsHash;
    }

    public StoriesRequestKey(String cacheId, String feed, String tagsHash, boolean isFavorite) {
        this.cacheId = cacheId;
        this.feed = feed;
        this.tagsHash = tagsHash;
        this.isFavorite = isFavorite;
    }

    public String cacheId() {
        return cacheId;
    }

    public String feed() {
        return feed;
    }

    public String tagsHash() {
        return tagsHash;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoriesRequestKey key = (StoriesRequestKey) o;
        return isFavorite == key.isFavorite &&
                Objects.equals(cacheId, key.cacheId) &&
                Objects.equals(feed, key.feed) &&
                Objects.equals(tagsHash, key.tagsHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cacheId, feed, tagsHash, isFavorite);
    }
}
