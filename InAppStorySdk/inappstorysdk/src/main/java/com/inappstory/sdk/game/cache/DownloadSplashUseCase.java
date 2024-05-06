package com.inappstory.sdk.game.cache;

import android.webkit.URLUtil;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.GameSplashUseCase;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;

public class DownloadSplashUseCase {
    GameSplashScreen splashScreen;
    FilesDownloadManager filesDownloadManager;
    String oldSplashPath;
    String gameId;

    public DownloadSplashUseCase(
            FilesDownloadManager filesDownloadManager,
            GameSplashScreen splashScreen,
            String oldSplashPath,
            String gameId
    ) {
        this.splashScreen = splashScreen;
        this.filesDownloadManager = filesDownloadManager;
        this.oldSplashPath = oldSplashPath;
        this.gameId = gameId;
    }

    @WorkerThread
    void download(
            final UseCaseCallback<File> splashScreenCallback
    ) {
        final FileChecker fileChecker = new FileChecker();
        File oldSplash = null;
        if (oldSplashPath != null) {
            oldSplash = new File(oldSplashPath);
            if (splashScreen == null || !URLUtil.isValidUrl(splashScreen.url)) {
                KeyValueStorage.removeString("gameInstanceSplash_" + gameId);
                if (oldSplash.exists()) {
                    oldSplash.deleteOnExit();
                }
                splashScreenCallback.onError("splash screen is not valid");
                return;
            }

            if (fileChecker.checkWithShaAndSize(
                    oldSplash,
                    splashScreen.size,
                    splashScreen.sha1,
                    false
            )) {
                splashScreenCallback.onError("splash already downloaded");
                return;
            }
        }
        if (splashScreen == null || !URLUtil.isValidUrl(splashScreen.url)) {
            splashScreenCallback.onError("splash screen is not valid");
            return;
        }
        GameSplashUseCase gameSplashUseCase =
                new GameSplashUseCase(filesDownloadManager, splashScreen);
        DownloadFileState fileState = gameSplashUseCase.getFile();
        if (fileState != null) {
            splashScreenCallback.onSuccess(fileState.file);
        } else  {
            splashScreenCallback.onError("Can't download splash");
        }
    }
}
