package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.core.network.content.models.SessionAsset;

import java.util.List;

public interface IASSessionAssetsHolder {

    void addKeys(List<SessionAsset> cacheObjects);
    void add(SessionAsset cacheObject);
    void addContentIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeContentIsReadyCallback(SessionAssetsIsReadyCallback callback);
    boolean checkIfContentIsReady();
    void contentIsCleared();
}
