package com.inappstory.sdk.stories.callbacks;

public interface ShareCallback {
    void onShare(String url, String title, String description, String id);
}