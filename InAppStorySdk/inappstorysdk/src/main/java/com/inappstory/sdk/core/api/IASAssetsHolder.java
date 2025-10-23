package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;

import java.util.List;

public interface IASAssetsHolder {
    List<SessionAsset> assets();
    void downloadAssets();
    void setAssets(List<SessionAsset> assets);
    boolean assetsIsDownloaded();
    void addAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void checkOrAddAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void clear();
}
