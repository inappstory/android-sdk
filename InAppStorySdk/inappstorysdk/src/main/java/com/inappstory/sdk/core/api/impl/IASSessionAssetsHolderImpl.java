package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.api.IASSessionAssetsHolder;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.HashMap;
import java.util.List;

public class IASSessionAssetsHolderImpl implements IASSessionAssetsHolder {

    private final HashMap<String, SessionAsset> cacheObjects = new HashMap<>();
    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();

    @Override
    public void addSessionAssetsKeys(List<SessionAsset> cacheObjects) {

    }

    @Override
    public void addSessionAsset(SessionAsset cacheObject) {

    }

    @Override
    public void addSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {

    }

    @Override
    public void removeSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {

    }

    @Override
    public boolean checkIfSessionAssetsIsReady() {
        return false;
    }

    @Override
    public void assetsIsCleared() {

    }

    @Override
    public boolean checkIfSessionAssetsIsReady(FilesDownloadManager filesDownloadManager) {
        return false;
    }
}
