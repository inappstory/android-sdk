package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;

import java.util.List;
import java.util.Set;

public interface IASAssetsHolder {
    String JS_FORMAT = "js";
    String CSS_FORMAT = "css";


    List<SessionAsset> assets();
    void downloadAssets();
    void reloadAssets(SessionAssetsIsReadyCallback callback);
    void setAssets(List<SessionAsset> assets);
    boolean assetsIsDownloaded();
    boolean assetsIsDownloaded(Set<String> contentAssetKeys);
    void addAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void checkOrAddAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void clear();
}
