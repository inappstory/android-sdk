package com.inappstory.sdk.game.preload;

import com.inappstory.sdk.game.cache.DownloadSplashUseCase;
import com.inappstory.sdk.game.cache.GetLocalSplashUseCase;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.utils.KeyValueStorage;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

public class LoadGameSplashesUseCase {

    private final List<IGameCenterData> gamesData;
    private final DownloadInterruption interruption;


    public LoadGameSplashesUseCase(
            List<IGameCenterData> gamesData,
            DownloadInterruption interruption
    ) {
        this.gamesData = gamesData;
        this.interruption = interruption;
    }

    public void download(IDownloadAllSplashesCallback callback) {
        downloadSplash(gamesData.listIterator(), callback);
    }

    private void downloadSplash(
            final ListIterator<IGameCenterData> gamesDataIterator,
            final IDownloadAllSplashesCallback callback
    ) {
        if (interruption.active) return;
        if (gamesDataIterator.hasNext()) {
            final IGameCenterData gameData = gamesDataIterator.next();
            final String[] oldSplashPath = {null};
            GetLocalSplashUseCase getLocalSplashUseCase = new GetLocalSplashUseCase(gameData.id());
            getLocalSplashUseCase.get(new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {

                }

                @Override
                public void onSuccess(File result) {
                    oldSplashPath[0] = result.getAbsolutePath();
                }
            });
            DownloadSplashUseCase downloadSplashUseCase = new DownloadSplashUseCase(
                    gameData.splashScreen(),
                    oldSplashPath[0],
                    gameData.id()
            );
            downloadSplashUseCase.download(new UseCaseCallback<File>() {
                @Override
                public void onError(String message) {
                    downloadSplash(gamesDataIterator, callback);
                }

                @Override
                public void onSuccess(File result) {
                    KeyValueStorage.saveString(
                            "gameInstanceSplash_" + gameData.id(),
                            result.getAbsolutePath()
                    );

                    downloadSplash(gamesDataIterator, callback);
                }
            });
        } else {
            callback.onDownloaded();
        }
    }

}
