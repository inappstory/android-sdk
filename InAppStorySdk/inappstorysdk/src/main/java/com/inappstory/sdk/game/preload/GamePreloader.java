package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.cache.DownloadInterruption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePreloader implements IGamePreloader {

    private boolean active = false;

    public GamePreloader() {

    }

    LoadGameSplashesUseCase splashesUseCase;
    LoadGameFilesUseCase gameFilesUseCase;

    Map<String, IGameCenterData> loadedData = null;

    @Override
    public void launch() {
        if (!active) return;
        if (loadedData == null) {
            GetGamePreloadModelsUseCase getGameModelsUseCase = new GetGamePreloadModelsUseCase();
            getGameModelsUseCase.get(new IGetGamePreloadModelsCallback() {
                @Override
                public void onSuccess(List<IGameCenterData> data) {
                    if (data != null) {
                        loadedData = new HashMap<>();
                        for (IGameCenterData dataItem : data) {
                            loadedData.put(dataItem.id(), dataItem);
                        }
                        loadFiles();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            loadFiles();
        }
    }

    DownloadInterruption interruption;

    private void loadSplashes(IDownloadAllSplashesCallback callback) {
        synchronized (useCaseCreateLock) {
            interruption = new DownloadInterruption();
            splashesUseCase = new LoadGameSplashesUseCase(
                    new ArrayList<>(loadedData.values()),
                    interruption
            );
        }
        splashesUseCase.download(callback);
    }

    public SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback;

    private void loadFiles() {
        loadSplashes(
                new IDownloadAllSplashesCallback() {
                    @Override
                    public void onDownloaded() {
                        synchronized (useCaseCreateLock) {
                            gameFilesUseCase = new LoadGameFilesUseCase(
                                    new ArrayList<>(loadedData.values()),
                                    interruption
                            );
                            gameFilesUseCase.download(successUseCaseCallback);
                        }
                    }
                }
        );
    }

    private final Object useCaseCreateLock = new Object();

    @Override
    public void pause() {
        if (!active) return;
        synchronized (useCaseCreateLock) {
            interruption.active = true;
        }
    }

    @Override
    public void restart() {
        if (!active) return;
        launch();
    }

    @Override
    public void active(boolean active) {
        this.active = active;
    }
}
