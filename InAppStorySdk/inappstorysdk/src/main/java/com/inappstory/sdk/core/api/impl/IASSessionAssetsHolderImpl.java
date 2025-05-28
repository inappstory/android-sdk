package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.api.IASSessionAssetsHolder;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.HashMap;
import java.util.List;

public class IASSessionAssetsHolderImpl implements IASSessionAssetsHolder {

    private final HashMap<String, SessionAsset> cacheObjects = new HashMap<>();
    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();


    @Override
    public void addKeys(List<SessionAsset> cacheObjects) {

    }

    @Override
    public void add(SessionAsset cacheObject) {

    }

    @Override
    public void addContentIsReadyCallback(SessionAssetsIsReadyCallback callback) {

    }

    @Override
    public void removeContentIsReadyCallback(SessionAssetsIsReadyCallback callback) {

    }

    @Override
    public boolean checkIfContentIsReady() {
        return false;
    }

    @Override
    public void contentIsCleared() {

    }

    @Override
    public boolean checkIfContentIsReady(FilesDownloadManager filesDownloadManager) {
        return false;
    }
}
