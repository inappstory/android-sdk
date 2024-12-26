package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.core.network.content.models.SessionAsset;

import java.util.List;

public interface IASContentPreload {
    void downloadInAppMessages(List<String> inAppMessageIds);

    IGamePreloader getGamePreloader();

    void restartGamePreloader();
}
