package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.core.network.content.models.SessionAsset;

import java.util.List;

public interface IASContentPreload {
    void downloadIASMessages();

    void downloadSessionAssets(
            List<SessionAsset> sessionAssets
    );

    IGamePreloader getGamePreloader();

    void restartGamePreloader();


}
