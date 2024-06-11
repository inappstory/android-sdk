package com.inappstory.sdk.game.cache;

import android.webkit.URLUtil;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.interfaces.IDownloadResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.GameSplashUseCase;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;

public class DownloadSplashUseCase {
    IDownloadResource resource;
    FilesDownloadManager filesDownloadManager;
    String oldSplashPath;
    String gameId;
    String keyValueStorageKey;

    public DownloadSplashUseCase(
            FilesDownloadManager filesDownloadManager,
            IDownloadResource resource,
            String oldSplashPath,
            String keyValueStorageKey,
            String gameId
    ) {
        this.resource = resource;
        this.filesDownloadManager = filesDownloadManager;
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
                KeyValueStorage.removeString(keyValueStorageKey + gameId);
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
                new GameSplashUseCase(filesDownloadManager, resource);
        DownloadFileState fileState = gameSplashUseCase.getFile();
        if (fileState != null) {
            if (fileState.file.exists()) {
                if (oldSplashPath != null) {
                    oldSplash = new File(oldSplashPath);
                    KeyValueStorage.removeString(keyValueStorageKey + gameId);
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
