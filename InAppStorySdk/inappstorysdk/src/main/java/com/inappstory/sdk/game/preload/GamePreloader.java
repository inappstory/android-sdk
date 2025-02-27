package com.inappstory.sdk.game.preload;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePreloader implements IGamePreloader {

    private boolean active = false;
    private final boolean useAnimSplash;
    private final IASCore core;

    public GamePreloader(
            IASCore core,
            FilesDownloadManager filesDownloadManager,
                         boolean useAnimSplash,
            SuccessUseCaseCallback<IGameCenterData> successUseCaseCallback
    ) {
        this.core = core;
        this.filesDownloadManager = filesDownloadManager;
        this.useAnimSplash = useAnimSplash;
        this.successUseCaseCallback = successUseCaseCallback;
    }

    LoadGameSplashesUseCase splashesUseCase;
    LoadGameFilesUseCase gameFilesUseCase;
    private final FilesDownloadManager filesDownloadManager;

    Map<String, IGameCenterData> loadedData = null;

    @Override
    public void launch() {
        if (!active) return;
        if (loadedData == null) {
            GetGamePreloadModelsUseCase getGameModelsUseCase = new GetGamePreloadModelsUseCase(core);
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

    DownloadInterruption interruption = new DownloadInterruption();

    private void loadSplashes(IDownloadAllSplashesCallback callback) {
        synchronized (useCaseCreateLock) {
            interruption = new DownloadInterruption();
            splashesUseCase = new LoadGameSplashesUseCase(
                    core,
                    new ArrayList<>(loadedData.values()),
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
                        synchronized (useCaseCreateLock) {
                            gameFilesUseCase = new LoadGameFilesUseCase(
                                    core,
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
        synchronized (useCaseCreateLock) {
            interruption.active = true;
        }
    }

    @Override
    public void restart() {
        if (!active) return;
        loadedData = null;
        launch();
    }

    @Override
    public void active(boolean active) {
        this.active = active;
    }
}
