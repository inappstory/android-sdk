package com.inappstory.sdk.core.api;

import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.List;

public interface IASSessionAssetsHolder {

    void addKeys(List<SessionAsset> cacheObjects);
    void add(SessionAsset cacheObject);
    void addContentIsReadyCallback(SessionAssetsIsReadyCallback callback);
    void removeContentIsReadyCallback(SessionAssetsIsReadyCallback callback);
    boolean checkIfContentIsReady();
    void contentIsCleared();
    boolean checkIfContentIsReady(FilesDownloadManager filesDownloadManager);
}
