package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;

public class GetLocalSplashUseCase {
    private String gameId;
    private String keyValueStorageKey;
    private final IASCore core;

    public GetLocalSplashUseCase(
            IASCore core,
                                 String gameId,
                                 String keyValueStorageKey
    ) {
        this.core = core;
        this.gameId = gameId;
        this.keyValueStorageKey = keyValueStorageKey;
    }

    @WorkerThread
    public void get(UseCaseCallback<File> splashScreenCallback) {
        String splashPath = core.keyValueStorage().getString(keyValueStorageKey + gameId);
        if (splashPath != null) {
            File splash = new File(splashPath);
            if (splash.exists()) {
                splashScreenCallback.onSuccess(splash);
                return;
            }
        }
        splashScreenCallback.onError("No cached appropriate splash");
    }

}
