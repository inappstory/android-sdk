package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;

public class GetLocalSplashUseCase {
    private String gameId;

    public GetLocalSplashUseCase(String gameId) {
        this.gameId = gameId;
    }

    @WorkerThread
    public void get(UseCaseCallback<File> splashScreenCallback) {
        String splashPath = KeyValueStorage.getString("gameInstanceSplash_" + gameId);
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
