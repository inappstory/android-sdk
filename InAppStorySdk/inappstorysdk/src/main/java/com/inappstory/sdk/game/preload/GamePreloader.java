package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePreloader implements IGamePreloader {

    private final boolean useAnimSplash;

    public GamePreloader(
            FilesDownloadManager filesDownloadManager,
                         boolean useAnimSplash,
            SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback
    ) {
        this.filesDownloadManager = filesDownloadManager;
        this.useAnimSplash = useAnimSplash;
        this.successUseCaseCallback = successUseCaseCallback;
    }

    LoadGameSplashesUseCase splashesUseCase;
    LoadGameFilesUseCase gameFilesUseCase;
    private final FilesDownloadManager filesDownloadManager;

    Map<String, IGameCenterData> loadedData = null;

    private void launch() {
        if (loadedData == null) {
            GetGamePreloadModelsUseCase getGameModelsUseCase = new GetGamePreloadModelsUseCase();
            getGameModelsUseCase.get(new IGetGamePreloadModelsCallback() {
                @Override
                public void onSuccess(List<IGameCenterData> data) {
                    if (cannotBeUsed()) return;
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
        if (cannotBeUsed()) return;
        synchronized (useCaseCreateLock) {
            interruption = new DownloadInterruption();
            splashesUseCase = new LoadGameSplashesUseCase(
                    new ArrayList<>(loadedData.values()),
                    filesDownloadManager,
                    interruption
            );
        }
        splashesUseCase.download(callback, useAnimSplash);
    }

    public SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback;

    private void loadFiles() {
        loadSplashes(
                new IDownloadAllSplashesCallback() {
                    @Override
                    public void onDownloaded() {
                        if (cannotBeUsed()) return;
                        synchronized (useCaseCreateLock) {
                            gameFilesUseCase = new LoadGameFilesUseCase(
                                    new ArrayList<>(loadedData.values()),
                                    filesDownloadManager,
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
        synchronized (useCaseCreateLock) {
            interruption.active = true;
        }
    }


    @Override
    public void resume() {
        synchronized (useCaseCreateLock) {
            interruption.active = false;
        }
        launch();
    }

    private boolean cannotBeUsed() {
        synchronized (useCaseCreateLock) {
            return (interruption.active);
        }
    }

    @Override
    public void restart() {
        if (cannotBeUsed()) return;
        synchronized (useCaseCreateLock) {
            boolean currentInterruptionStatus = interruption.active;
            interruption.active = true;
            interruption = new DownloadInterruption();
            interruption.active = currentInterruptionStatus;
        }
        loadedData = null;
        launch();
    }
}
