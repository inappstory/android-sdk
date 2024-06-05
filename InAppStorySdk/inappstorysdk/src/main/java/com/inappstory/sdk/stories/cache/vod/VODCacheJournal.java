package com.inappstory.sdk.stories.cache.vod;

import java.util.HashMap;
import java.util.Map;

public class VODCacheJournal {
    private Map<String, VODCacheJournalItem> vodItems = new HashMap<>();

    public VODCacheJournalItem getItem(String key) {
        return vodItems.get(key);
    }

    public void putItem(VODCacheJournalItem item) {
        vodItems.put(item.uniqueKey, item);
    }

    void readJournal() {

    }

    void writeJournal() {

    }
}
