package com.inappstory.sdk.core.api;

import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;

import java.util.List;

public interface IASContentPreload {
    void downloadInAppMessages(
            InAppMessagePreloadSettings preloadSettings,
            InAppMessageLoadCallback callback
    );

    void restartGamePreloader();

    void pauseGamePreloader();

    void resumeGamePreloader();
}
