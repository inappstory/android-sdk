package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;

import java.util.List;

public interface IASContentPreload {
    void downloadInAppMessages(List<String> inAppMessageIds, InAppMessageLoadCallback callback);

    void restartGamePreloader();
    void pauseGamePreloader();
    void resumeGamePreloader();
}
