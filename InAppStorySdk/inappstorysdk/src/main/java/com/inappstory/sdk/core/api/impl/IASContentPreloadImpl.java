package com.inappstory.sdk.core.api.impl;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASContentPreload;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.preload.GamePreloader;
import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetUseCase;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.File;
import java.util.List;

public class IASContentPreloadImpl implements IASContentPreload {
    private final IASCore core;

    public IGamePreloader getGamePreloader() {
        return gamePreloader;
    }

    private IGamePreloader gamePreloader;

    public IASContentPreloadImpl(IASCore core) {
        this.core = core;
        gamePreloader = new GamePreloader(
                core,
                core.contentLoader().filesDownloadManager(),
                core.externalUtilsAPI().hasLottieAnimation(),
                new SuccessUseCaseCallback<IGameCenterData>() {
                    @Override
                    public void onSuccess(final IGameCenterData result) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("IAS_Game_Preloading", "Game " + result.id() + " is loaded");
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void downloadSessionAssets(List<SessionAsset> sessionAssets) {
        if (sessionAssets != null) {
            ISessionHolder sessionHolder = core.sessionManager().getSession();
            sessionHolder.addSessionAssetsKeys(sessionAssets);
            for (SessionAsset sessionAsset : sessionAssets) {
                downloadSessionAsset(sessionAsset, sessionHolder);
            }
        }
    }

    private void downloadSessionAsset(final SessionAsset sessionAsset, final ISessionHolder sessionHolder) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                new SessionAssetUseCase(core,
                        new UseCaseCallback<File>() {
                            @Override
                            public void onError(String message) {

                            }

                            @Override
                            public void onSuccess(File result) {
                                sessionHolder.addSessionAsset(sessionAsset);
                                sessionHolder.checkIfSessionAssetsIsReadyAsync();
                            }
                        },
                        sessionAsset
                ).getFile();
            }
        });
    }

    @Override
    public void restartGamePreloader() {
        gamePreloader.pause();
        gamePreloader.active(true);
        gamePreloader.restart();
    }
}
