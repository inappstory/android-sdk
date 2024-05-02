package com.inappstory.sdk.lrudiskcache;

public class CacheMappingJournalItem {
    private String cacheKey;
    private String filename;
    private String sha1;
    private String mimeType;
    private String replaceContentKey;
    private String extension;
    private long time;
    private long size;
    private long downloadedSize;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheMappingJournalItem item = (CacheMappingJournalItem) o;
        if (!replaceContentKey.equals(item.replaceContentKey)) return false;
        if (!cacheKey.equals(item.cacheKey)) return false;
        if (!mimeType.equals(item.mimeType)) return false;
        if (!sha1.equals(item.sha1)) return false;
        return filename.equals(item.filename);
    }

    public long getTime() {
        return time;
    }

    public CacheMappingJournalItem updateTime() {
        this.time = System.currentTimeMillis();
        return this;
    }

    @Override
    public int hashCode() {
        int result = replaceContentKey.hashCode();
        result = 31 * result + mimeType.hashCode();
        result = 31 * result + cacheKey.hashCode();
        result = 31 * result + sha1.hashCode();
        result = 31 * result + filename.hashCode();
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }
}
