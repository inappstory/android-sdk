package com.inappstory.sdk.stories.cache;

public interface DownloadPageCallback {
    boolean downloadFile(String url, String storyId, int index);
}
