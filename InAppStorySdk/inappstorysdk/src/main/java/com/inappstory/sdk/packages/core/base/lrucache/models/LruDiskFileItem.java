package com.inappstory.sdk.packages.core.base.lrucache.models;

public class LruDiskFileItem implements ILruDiskFileItem {
    String filePath;
    long downloadedSize;
    long fullSize;
    private String sha1;

    public LruDiskFileItem(String filePath, String sha1, long downloadedSize, long fullSize) {
        this.filePath = filePath;
        this.sha1 = sha1;
        this.downloadedSize = downloadedSize;
        this.fullSize = fullSize;
    }

    public LruDiskFileItem(String filePath, long downloadedSize, long fullSize) {
        this.filePath = filePath;
        this.downloadedSize = downloadedSize;
        this.fullSize = fullSize;
    }

    @Override
    public String filePath() {
        return filePath;
    }

    @Override
    public long downloadedSize() {
        return downloadedSize;
    }

    @Override
    public String sha1() {
        return sha1;
    }

    @Override
    public long fullSize() {
        return fullSize;
    }
}
