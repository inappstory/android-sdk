package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterDataListType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetGamePreloadModelsUseCase {
    public void get(final IGetGamePreloadModelsCallback callback) {
        InAppStoryService service = InAppStoryService.getInstance();
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (service == null || networkClient == null) {
            callback.onError(null);
            return;
        }
        networkClient.enqueue(networkClient.getApi().getPreloadGames(),
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
        callback.onSuccess(new ArrayList<IGameCenterData>());
    }
}
