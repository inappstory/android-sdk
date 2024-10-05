package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.game.cache.DownloadSplashUseCase;
import com.inappstory.sdk.game.cache.GetLocalSplashUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.utils.GameConstants;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LoadGameSplashesUseCase {

    private final List<IGameCenterData> gamesData;
    private final DownloadInterruption interruption;
    private final FilesDownloadManager filesDownloadManager;


    public LoadGameSplashesUseCase(
            List<IGameCenterData> gamesData,
            FilesDownloadManager filesDownloadManager,
            DownloadInterruption interruption
    ) {
        this.gamesData = gamesData;
        this.filesDownloadManager = filesDownloadManager;
        this.interruption = interruption;
    }

    public void download(
            IDownloadAllSplashesCallback callback,
            boolean useAnimSplash
    ) {
        final Map<String, String> splashesKeyValueStorageKeys = GameConstants.getSplashesKeys(useAnimSplash);
        downloadSplash(
                gamesData.listIterator(),
                useAnimSplash,
                callback
        );
    }

    private void downloadSplash(
            final ListIterator<IGameCenterData> gamesDataIterator,
            final boolean useAnimSplash,
            final IDownloadAllSplashesCallback callback
    ) {
        if (interruption.active) return;
        if (gamesDataIterator.hasNext()) {

            final Map<String, File> localSplashFiles = new HashMap<>();
            final IGameCenterData gameData = gamesDataIterator.next();

            GetLocalSplashUseCase getLocalStaticSplashUseCase = new GetLocalSplashUseCase(
                    gameData.id(),
                    GameConstants.SPLASH_STATIC_KV
            );

            GetLocalSplashUseCase getLocalAnimSplashUseCase = new GetLocalSplashUseCase(
                    gameData.id(),
                    GameConstants.SPLASH_STATIC_KV
            );

            getLocalStaticSplashUseCase.get(new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {
                }

                @Override
                public void onSuccess(File result) {
                    localSplashFiles.put(GameConstants.SPLASH_STATIC, result);
                }
            });
            getLocalAnimSplashUseCase.get(new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {
                }

                @Override
                public void onSuccess(File result) {
                    localSplashFiles.put(GameConstants.SPLASH_ANIM, result);
                }
            });
            File staticFile = localSplashFiles.get(GameConstants.SPLASH_STATIC);
            DownloadSplashUseCase downloadStaticSplashUseCase = new DownloadSplashUseCase(
                    filesDownloadManager,
                    gameData.splashScreen(),
                    staticFile != null ? staticFile.getAbsolutePath() : null,
                    GameConstants.SPLASH_STATIC_KV,
                    gameData.id()
            );

            File animFile = localSplashFiles.get(GameConstants.SPLASH_ANIM);
            final DownloadSplashUseCase downloadAnimSplashUseCase = new DownloadSplashUseCase(
                    filesDownloadManager,
                    gameData.splashAnimation(),
                    animFile != null ? animFile.getAbsolutePath() : null,
                    GameConstants.SPLASH_ANIM_KV,
                    gameData.id()
            );
            final UseCaseCallback<File> downloadAnimSplashCallback = new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {
                    KeyValueStorage.saveString(
                            GameConstants.SPLASH_ANIM_KV + gameData.id(),
                            ""
                    );
                    downloadSplash(
                            gamesDataIterator,
                            useAnimSplash,
                            callback
                    );
                }

                @Override
                public void onSuccess(File result) {
                    if (result == null) return;
                    KeyValueStorage.saveString(
                            GameConstants.SPLASH_ANIM_KV + gameData.id(),
                            result.getAbsolutePath()
                    );

                    try {
                        KeyValueStorage.saveString(
                                GameConstants.SPLASH_ANIM_KV_SETTINGS + gameData.id(),
                                JsonParser.getJson(gameData.splashAnimation())
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    downloadSplash(
                            gamesDataIterator,
                            useAnimSplash,
                            callback
                    );
                }
            };

            downloadStaticSplashUseCase.download(new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {
                    if (useAnimSplash) {
                        downloadAnimSplashUseCase.download(downloadAnimSplashCallback);
                    } else {
                        downloadSplash(
                                gamesDataIterator,
                                false,
                                callback
                        );
                    }
                }

                @Override
                public void onSuccess(File result) {
                    if (result == null) return;
                    KeyValueStorage.saveString(
                            GameConstants.SPLASH_STATIC_KV + gameData.id(),
                            result.getAbsolutePath()
                    );

                    if (useAnimSplash) {
                        downloadAnimSplashUseCase.download(downloadAnimSplashCallback);
                    } else {
                        downloadSplash(
                                gamesDataIterator,
                                false,
                                callback
                        );
                    }
                }
            });
        } else {
            callback.onDownloaded();
        }
    }

}