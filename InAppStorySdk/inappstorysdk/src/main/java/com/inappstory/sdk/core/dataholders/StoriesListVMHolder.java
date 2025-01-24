package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.storieslist.StoriesRequestResultHolder;
import com.inappstory.sdk.core.storieslist.StoriesRequestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoriesListVMHolder implements IStoriesListVMHolder {
    private final IASCore core;
    private final Map<StoriesRequestKey, StoriesRequestResultHolder> storiesRequestResults = new HashMap<>();

    private final Object lock = new Object();

    public StoriesListVMHolder(IASCore core) {
        this.core = core;
    }

    @Override
    public StoriesRequestResult getStoriesRequestResult(StoriesRequestKey key) {
        if (key == null) return null;
        synchronized (lock) {
            StoriesRequestResultHolder storiesRequestResultHolder = storiesRequestResults.get(key);
            if (storiesRequestResultHolder != null) return storiesRequestResultHolder.getState();
            return null;
        }
    }

    @Override
    public void setStoriesRequestResult(StoriesRequestKey newKey, StoriesRequestResult state) {
        if (newKey == null) return;
        synchronized (lock) {
            Iterator<Map.Entry<StoriesRequestKey, StoriesRequestResultHolder>> entryIterator = storiesRequestResults.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<StoriesRequestKey, StoriesRequestResultHolder> entry = entryIterator.next();
                StoriesRequestKey key = entry.getKey();
                if (key.isFavorite()) continue;
                if (Objects.equals(key.cacheId(), newKey.cacheId()) &&
                        Objects.equals(key.cacheId(), newKey.feed()) &&
                        !Objects.equals(key.tagsHash(), newKey.tagsHash())) {
                    entryIterator.remove();
                }
            }
            StoriesRequestResultHolder storiesRequestResultHolder = storiesRequestResults.get(newKey);
            if (storiesRequestResultHolder == null) {
                storiesRequestResults.put(newKey, new StoriesRequestResultHolder(state));
            } else {
                storiesRequestResultHolder.setState(state);
            }
            if (!newKey.isFavorite() && !(newKey.cacheId() == null || newKey.cacheId().isEmpty())) {
                StoriesRequestKey keyWithoutCacheId = new StoriesRequestKey(null, newKey.feed(), newKey.tagsHash());
                StoriesRequestResultHolder storiesRequestNoCacheIdResultHolder = storiesRequestResults.get(keyWithoutCacheId);
                if (storiesRequestNoCacheIdResultHolder == null) {
                    storiesRequestResults.put(keyWithoutCacheId, new StoriesRequestResultHolder(state));
                } else {
                    storiesRequestNoCacheIdResultHolder.setState(state);
                }
            }
        }
    }

    @Override
    public void removeResultById(String cacheId) {
        if (cacheId == null) return;
        synchronized (lock) {
            Iterator<Map.Entry<StoriesRequestKey, StoriesRequestResultHolder>> entryIterator = storiesRequestResults.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<StoriesRequestKey, StoriesRequestResultHolder> entry = entryIterator.next();
                StoriesRequestKey key = entry.getKey();
                if (key.isFavorite()) continue;
                if (Objects.equals(key.cacheId(), cacheId)) {
                    entryIterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void removeResultByFeed(String feed) {
        if (feed == null) return;
        synchronized (lock) {
            Iterator<Map.Entry<StoriesRequestKey, StoriesRequestResultHolder>> entryIterator = storiesRequestResults.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<StoriesRequestKey, StoriesRequestResultHolder> entry = entryIterator.next();
                StoriesRequestKey key = entry.getKey();
                if (key.isFavorite()) continue;
                if (Objects.equals(key.feed(), feed)) {
                    entryIterator.remove();
                }
            }
        }
    }

    @Override
    public void removeResultByIdAndFeed(String cacheId, String feed) {
        if (feed == null) return;
        synchronized (lock) {
            Iterator<Map.Entry<StoriesRequestKey, StoriesRequestResultHolder>> entryIterator = storiesRequestResults.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<StoriesRequestKey, StoriesRequestResultHolder> entry = entryIterator.next();
                StoriesRequestKey key = entry.getKey();
                if (key.isFavorite()) continue;
                if (Objects.equals(key.cacheId(), cacheId) && feed.equals(key.feed())) {
                    entryIterator.remove();
                }
            }
        }
    }

    @Override
    public void removeFavoriteResult() {
        synchronized (lock) {
            Iterator<Map.Entry<StoriesRequestKey, StoriesRequestResultHolder>> entryIterator = storiesRequestResults.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<StoriesRequestKey, StoriesRequestResultHolder> entry = entryIterator.next();
                StoriesRequestKey key = entry.getKey();
                if (key.isFavorite()) {
                    entryIterator.remove();
                }
            }
        }
    }


    @Override
    public void clear() {
        synchronized (lock) {
            storiesRequestResults.clear();
        }
    }
}
