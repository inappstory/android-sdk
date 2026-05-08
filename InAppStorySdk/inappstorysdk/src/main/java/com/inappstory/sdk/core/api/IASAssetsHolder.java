package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;

import java.util.List;

public interface IASAssetsHolder {
    String JS_FORMAT = "js";
    String CSS_FORMAT = "css";


    List<SessionAsset> assets();
    List<SessionAsset> jsAssets();
    List<SessionAsset> cssAssets();
    List<String> layoutAssets();
    void downloadAssets();
    void reloadAssets(SessionAssetsIsReadyCallback callback);
    void setAssets(List<SessionAsset> assets, String contentLayout);
    boolean assetsIsDownloaded();
    boolean assetsIsDownloaded(List<String> assetKeys);
    void addAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void checkOrAddAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void clear();
}
