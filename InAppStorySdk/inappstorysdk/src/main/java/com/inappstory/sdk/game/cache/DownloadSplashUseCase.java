package com.inappstory.sdk.game.cache;

import android.webkit.URLUtil;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.core.dataholders.models.IDownloadResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.usecases.GameSplashUseCase;

import java.io.File;

public class DownloadSplashUseCase {
    private final IDownloadResource resource;
    private final String oldSplashPath;
    private final String gameId;
    private final String keyValueStorageKey;
    private final IASCore core;

    public DownloadSplashUseCase(
            IASCore core,
            IDownloadResource resource,
            String oldSplashPath,
            String keyValueStorageKey,
            String gameId
    ) {
        this.resource = resource;
        this.core = core;
        this.oldSplashPath = oldSplashPath;
        this.gameId = gameId;
        this.keyValueStorageKey = keyValueStorageKey;
    }

    @WorkerThread
    public void download(
            final UseCaseCallback<File> splashScreenCallback
    ) {
        final FileChecker fileChecker = new FileChecker();
        File oldSplash = null;
        if (oldSplashPath != null) {
            oldSplash = new File(oldSplashPath);
            if (resource == null || !URLUtil.isValidUrl(resource.url())) {
                core.keyValueStorage().removeString(keyValueStorageKey + gameId);
                if (oldSplash.exists()) {
                    oldSplash.deleteOnExit();
                }
                splashScreenCallback.onError("Splash screen is not valid");
                return;
            }

            if (fileChecker.checkWithShaAndSize(
                    oldSplash,
                    resource.size(),
                    resource.sha1(),
                    false
            )) {
                splashScreenCallback.onSuccess(oldSplash);
                return;
            }
        }
        if (resource == null || !URLUtil.isValidUrl(resource.url())) {
            splashScreenCallback.onError("Splash screen is not valid");
            return;
        }
        GameSplashUseCase gameSplashUseCase =
                new GameSplashUseCase(core, resource);
        DownloadFileState fileState = gameSplashUseCase.getFile();
        if (fileState != null) {
            if (fileState.file.exists()) {
                if (oldSplashPath != null) {
                    oldSplash = new File(oldSplashPath);
                    core.keyValueStorage().removeString(keyValueStorageKey + gameId);
                    if (oldSplash.exists()) {
                        oldSplash.deleteOnExit();
                    }
                }
            }
            splashScreenCallback.onSuccess(fileState.file);
        } else  {
            splashScreenCallback.onError("Can't download splash");
        }
    }
}
