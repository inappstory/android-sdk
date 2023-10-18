package com.inappstory.sdk.core.lrudiskcache;

public class CacheJournalItem {
    private String key;
    private String name;
    private long time;
    private long size;

    private long downloadedSize;

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public boolean isReady() {
        return downloadedSize == size;
    }


    public CacheJournalItem(String key, String name, long time, long size) {
        this.key = key;
        this.name = name;
        this.time = time;
        this.size = size;
        this.downloadedSize = size;
    }

    public CacheJournalItem(String key, String name, long time, long size, long downloadedSize) {
        this.key = key;
        this.name = name;
        this.time = time;
        this.size = size;
        this.downloadedSize = downloadedSize;
    }

    public void copy(CacheJournalItem item, long time) {
        set(item.key, item.name, time, item.size);
    }

    public void set(String key, String name, long time, long size) {
        this.key = key;
        this.name = name;
        this.time = time;
        this.size = size;
        this.downloadedSize = size;
    }


    public void set(String key, String name, long time, long size, long downloadedSize) {
        this.key = key;
        this.name = name;
        this.time = time;
        this.size = size;
        this.downloadedSize = downloadedSize;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheJournalItem item = (CacheJournalItem) o;
        if (time != item.time) return false;
        if (!key.equals(item.key)) return false;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
