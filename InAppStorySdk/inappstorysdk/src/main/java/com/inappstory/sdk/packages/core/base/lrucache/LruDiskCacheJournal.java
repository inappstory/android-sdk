package com.inappstory.sdk.packages.core.base.lrucache;

import com.inappstory.sdk.packages.core.base.lrucache.models.LruDiskCacheJournalItem;

public class LruDiskCacheJournal implements ILruCacheWithRemove<String, LruDiskCacheJournalItem> {
    @Override
    public void put(String key, LruDiskCacheJournalItem item) {

    }

    @Override
    public LruDiskCacheJournalItem get(String key) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void delete(String key) {

    }
}
