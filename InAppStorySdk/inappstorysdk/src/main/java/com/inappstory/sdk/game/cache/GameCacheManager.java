package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.NetworkCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.utils.SessionManager;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;

public class GameCacheManager {
    HashMap<String, CachedGame> cachedGames = new HashMap<>();

    public void getGame(String gameId, GameLoadCallback callback) {
        CachedGame game = cachedGames.get(gameId);
        if (game != null) {
            callback.onSuccess(game.data);
        } else {
            getGameFromGameCenter(gameId, callback);
        }
    }

    private void getGameFromGameCenter(final String gameId, final GameLoadCallback callback) {

        SessionManager.getInstance().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess() {
                NetworkClient.getApi().getGameByInstanceId(gameId).enqueue(
                        new NetworkCallback<GameCenterData>() {
                            @Override
                            public void onSuccess(final GameCenterData response) {
                                cachedGames.put(gameId, new CachedGame(response));
                                if (response.splashScreen != null) {
                                    try {
                                        Downloader.downloadOrGetFile(
                                                response.splashScreen.url,
                                                InAppStoryService.getInstance().getCommonCache(),
                                                null,
                                                new FileLoadProgressCallback() {
                                                    @Override
                                                    public void onProgress(int loadedSize, int totalSize) {

                                                    }

                                                    @Override
                                                    public void onSuccess(File file) {
                                                        callback.onSuccess(response);
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        callback.onSuccess(response);
                                                    }
                                                }
                                        );
                                    } catch (Exception ignored) {
                                    }
                                }
                            }

                            @Override
                            public Type getType() {
                                return GameCenterData.class;
                            }

                            @Override
                            public void onError(int code, String message) {
                                super.onError(code, message);
                            }

                            @Override
                            public void onTimeout() {
                                super.onTimeout();
                            }
                        });
            }

            @Override
            public void onError() {

            }
        });
    }
}
