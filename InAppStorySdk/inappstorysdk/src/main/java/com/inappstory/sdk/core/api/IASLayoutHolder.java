package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.cache.LayoutIsReadyCallback;

public interface IASLayoutHolder {

    String layout();
    boolean layoutIsDownloaded();
    void checkOrAddLayoutIsReadyCallback(LayoutIsReadyCallback callback);
    void removeLayoutIsReadyCallback(LayoutIsReadyCallback callback);
    void clear();
}
