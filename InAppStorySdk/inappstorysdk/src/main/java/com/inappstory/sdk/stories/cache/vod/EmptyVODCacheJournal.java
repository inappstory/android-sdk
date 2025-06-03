package com.inappstory.sdk.stories.cache.vod;

public class EmptyVODCacheJournal extends VODCacheJournal {

    public void clear() {
    }


    public EmptyVODCacheJournal() {
        super();
    }

    public VODCacheJournalItem getItem(String key) {
        return null;
    }

    public void putItem(VODCacheJournalItem item) {

    }

    public void readJournal() {

    }

    public void writeJournal() {

    }
}
