package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;

import java.util.List;

public interface IGetGamePreloadModelsCallback {
    void onSuccess(List<IGameCenterData> data);
    void onError(String error);
}
