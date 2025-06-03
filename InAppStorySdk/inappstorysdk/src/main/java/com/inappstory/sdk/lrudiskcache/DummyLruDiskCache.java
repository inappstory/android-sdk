package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.io.File;
import java.io.IOException;

public class DummyLruDiskCache extends LruDiskCache {


    public File getCacheDir() {
        return new File("");
    }

    DummyLruDiskCache(
    ) {
        super(null, null, null, 0, null);
    }


    public void put(CacheJournalItem item) throws IOException {

    }

    public void put(CacheJournalItem item, String type) throws IOException {

    }

    public void delete(CacheJournalItem item, String type) throws IOException {

    }

    public void delete(String key) throws IOException {

    }

    private void delete(String key, boolean writeJournal) throws IOException {

    }

    public void clearCache() throws IOException {

    }

    public long getCacheSize() {
        return 0;
    }

    public void setCacheSize(long cacheSize) {

    }

    public String getNameFromKey(String key) {
        return "";
    }

    public File getFileFromKey(String key) {
        return null;
    }

    public boolean hasKey(String key) {
        return false;
    }

    public File getFullFile(String key) {
        return null;
    }


    public File getFullFile(String key, String type) {
        return null;
    }

    public DownloadFileState get(String key) {
        return null;
    }

    public CacheJournalItem getJournalItem(String key, String type) {
        return null;
    }


    public CacheJournalItem getJournalItem(String key) {
        return null;
    }

    public DownloadFileState get(String key, String type) {
        return null;
    }

    private void keyIsValid(String key) {

    }
}
