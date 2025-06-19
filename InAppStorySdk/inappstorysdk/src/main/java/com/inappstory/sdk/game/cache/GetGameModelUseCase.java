package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.GameLaunchConfigObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;

public class GetGameModelUseCase {
    private final IASCore core;

    public GetGameModelUseCase(IASCore core) {
        this.core = core;
    }

    void get(final String gameId, final GameLoadCallback callback) {
        final NetworkClient networkClient = core.network();
        final boolean demoMode;
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) {
            callback.onError("");
            return;
        }
        demoMode = ((IASDataSettingsHolder) core.settingsAPI()).gameDemoMode();
        inAppStoryManager.iasCore().sessionManager().useOrOpenSession(
                new OpenSessionCallback() {
                    @Override
                    public void onSuccess(RequestLocalParameters sessionParameters) {
                        networkClient.enqueue(
                                networkClient.getApi().getGameByInstanceId(
                                        gameId,
                                        new GameLaunchConfigObject(demoMode)
                                ),
                                new NetworkCallback<GameCenterData>() {
                                    @Override
                                    public void onSuccess(final GameCenterData response) {
                                        callback.onCreateLog(response.loggerLevel());
                                        if (response.url == null ||
                                                response.url.isEmpty() ||
                                                response.initCode == null ||
                                                response.initCode.isEmpty()
                                        ) {
                                            callback.onError("Invalid game data");
                                            return;
                                        }
                                        callback.onSuccess(response);
                                    }

                                    @Override
                                    public Type getType() {
                                        return GameCenterData.class;
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        callback.onError(message);
                                    }

                                    @Override
                                    public void timeoutError() {
                                        callback.onError("Game loading run out of time");
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {
                        callback.onError("Open session error");
                    }
                }
        );
    }
}
