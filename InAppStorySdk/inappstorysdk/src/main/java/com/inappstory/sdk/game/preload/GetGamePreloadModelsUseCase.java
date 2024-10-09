package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterDataListType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetGamePreloadModelsUseCase {
    private final IASCore core;

    public GetGamePreloadModelsUseCase(IASCore core) {
        this.core = core;
    }

    public void get(final IGetGamePreloadModelsCallback callback) {
        core.network().enqueue(
                core.network().getApi().getPreloadGames(true),
                new NetworkCallback<List<PreloadGameCenterData>>() {
                    @Override
                    public void onSuccess(List<PreloadGameCenterData> response) {
                        List<IGameCenterData> result = new ArrayList<>();
                        if (response != null) {
                            result.addAll(response);
                        }
                        callback.onSuccess(result);
                    }

                    @Override
                    public Type getType() {
                        return new PreloadGameCenterDataListType();
                    }

                    @Override
                    public void onError(int code, String message) {
                        callback.onSuccess(new ArrayList<IGameCenterData>());
                    }
                }
        );
    }
}
