package com.inappstory.sdk.game.cache;

import android.webkit.URLUtil;

import androidx.annotation.WorkerThread;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.GameSplashScreen;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;

public class DownloadSplashUseCase {
    GameSplashScreen splashScreen;
    String oldSplashPath;
    String gameId;

    public DownloadSplashUseCase(GameSplashScreen splashScreen, String oldSplashPath, String gameId) {
        this.splashScreen = splashScreen;
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
        IASCore.getInstance().filesRepository.getGameSplash(
                splashScreen.url,
                splashScreen.size,
                splashScreen.sha1,
                new IFileDownloadCallback() {
                    @Override
                    public void onSuccess(String fileAbsolutePath) {
                        File file = new File(fileAbsolutePath);
                        splashScreenCallback.onSuccess(file);
                    }

                    @Override
                    public void onError(int errorCode, String error) {
                        splashScreenCallback.onError(error);
                    }
                }
        );
    }
}
