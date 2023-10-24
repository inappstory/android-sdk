package com.inappstory.sdk.game.cache;

import static com.inappstory.sdk.core.network.NetworkClient.NC_IS_UNAVAILABLE;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.NetworkClient;
import com.inappstory.sdk.core.network.callbacks.NetworkCallback;
import com.inappstory.sdk.core.repository.session.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.lang.reflect.Type;

public class GetGameModelUseCase {
    void get(final String gameId, final GameLoadCallback callback) {
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            callback.onError(NC_IS_UNAVAILABLE);
            return;
        }
        IASCoreManager.getInstance().getSession(new IGetSessionCallback<SessionDTO>() {
            @Override
            public void onSuccess(SessionDTO session) {
                networkClient.enqueue(
                        networkClient.getApi().getGameByInstanceId(gameId),
                        new NetworkCallback<GameCenterData>() {
                            @Override
                            public void onSuccess(final GameCenterData response) {
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
        });
    }
}
