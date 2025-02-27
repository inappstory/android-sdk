package com.inappstory.sdk.core.api.impl;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASContentPreload;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.inappmessages.InAppMessageFeedCallback;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.preload.GamePreloader;
import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetUseCase;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.File;
import java.util.List;

public class IASContentPreloadImpl implements IASContentPreload {
    private final IASCore core;

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
    public void downloadInAppMessages(List<String> inAppMessageIds, final InAppMessageLoadCallback callback) {
        String iamIds = null;
        if (inAppMessageIds != null && !inAppMessageIds.isEmpty()) {
            iamIds = TextUtils.join(",", inAppMessageIds);
        }
        new InAppMessagesUseCase(core, iamIds)
                .get(new InAppMessageFeedCallback() {
                    @Override
                    public void success(final List<IReaderContent> content) {
                        IASAssetsHolder assetsHolder = core.assetsHolder();
                        if (assetsHolder.assetsIsDownloaded()) {
                            downloadInAppMessagesContent(content, callback);
                        } else {
                            assetsHolder.addAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                                @Override
                                public void isReady() {
                                    downloadInAppMessagesContent(content, callback);
                                }
                            });
                        }
                    }

                    @Override
                    public void isEmpty() {
                        if (callback != null) callback.isEmpty();
                        core.callbacksAPI().useCallback(
                                IASCallbackType.IN_APP_MESSAGE_LOAD,
                                new UseIASCallback<InAppMessageLoadCallback>() {
                                    @Override
                                    public void use(@NonNull InAppMessageLoadCallback callback) {
                                        callback.isEmpty();
                                    }
                                }
                        );
                    }

                    @Override
                    public void error() {
                        if (callback != null) callback.loadError();
                        core.callbacksAPI().useCallback(
                                IASCallbackType.IN_APP_MESSAGE_LOAD,
                                new UseIASCallback<InAppMessageLoadCallback>() {
                                    @Override
                                    public void use(@NonNull InAppMessageLoadCallback callback) {
                                        callback.loadError();
                                    }
                                }
                        );
                    }
                });
    }

    private void downloadInAppMessageContent(final IReaderContent content, final InAppMessageLoadCallback callback) {
        core.contentLoader().inAppMessageDownloadManager().addInAppMessageTask(
                content.id(),
                ContentType.IN_APP_MESSAGE,
                callback
        );
    }

    private void downloadInAppMessagesContent(List<IReaderContent> content, InAppMessageLoadCallback callback) {
        if (core.contentLoader().inAppMessageDownloadManager().allContentIsLoaded()) {
            if (callback != null) {
                callback.allLoaded();
            }
            core.callbacksAPI().useCallback(
                    IASCallbackType.IN_APP_MESSAGE_LOAD,
                    new UseIASCallback<InAppMessageLoadCallback>() {
                        @Override
                        public void use(@NonNull InAppMessageLoadCallback callback) {
                            callback.allLoaded();
                        }
                    }
            );
            return;
        }
        for (IReaderContent contentItem : content) {
            downloadInAppMessageContent(contentItem, callback);
        }
    }

    @Override
    public void restartGamePreloader() {
        Log.e("ArchiveUseCase", "restartGamePreloader");
        gamePreloader.restart();
    }

    @Override
    public void pauseGamePreloader() {
        gamePreloader.pause();
    }

    @Override
    public void resumeGamePreloader() {
        gamePreloader.resume();
    }
}
