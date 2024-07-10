package com.inappstory.sdk.packages.core.base.lrucache.models;

public interface ILruDiskFileItem {
    String filePath();
    long downloadedSize();
    String sha1();
    long fullSize();
}
