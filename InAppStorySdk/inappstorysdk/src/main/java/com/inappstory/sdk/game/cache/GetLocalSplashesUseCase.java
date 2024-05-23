package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GetLocalSplashesUseCase {
    private String gameId;
    private Map<String, String> keyValueStorageKeys;

    public GetLocalSplashesUseCase(String gameId,
                                   Map<String, String> keyValueStorageKeys) {
        this.gameId = gameId;
        this.keyValueStorageKeys = keyValueStorageKeys;
    }

    @WorkerThread
    public void get(UseCaseCallback<Map<String, File>> splashesScreenCallback) {
        Map<String, File> splashFiles = new HashMap<>();
        for (String key : keyValueStorageKeys.keySet()) {
            String splashPath = KeyValueStorage.getString(keyValueStorageKeys.get(key) + gameId);
            if (splashPath != null) {
                File splash = new File(splashPath);
                if (splash.exists()) {
                    splashFiles.put(key, splash);
                    return;
                }
            }
        }
        if (splashFiles.size() == keyValueStorageKeys.size()) {
            splashesScreenCallback.onSuccess(splashFiles);
        } else {
            splashesScreenCallback.onError("No cached appropriate splashes");
        }
    }

}
