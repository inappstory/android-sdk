package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
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
import com.inappstory.sdk.game.preload.GamePreloader;
import com.inappstory.sdk.game.preload.IGamePreloader;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.stories.api.interfaces.IGameCenterData;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.util.ArrayList;
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
    public void downloadInAppMessages(
            InAppMessagePreloadSettings preloadSettings,
            final InAppMessageLoadCallback callback
    ) {
        String iamIds = null;
        List<String> filteredTags = null;
        if (preloadSettings != null) {
            if (preloadSettings.inAppMessageIds() != null && !preloadSettings.inAppMessageIds().isEmpty()) {
                iamIds = TextUtils.join(",", preloadSettings.inAppMessageIds());
            }
            if (preloadSettings.tags() != null) {
                filteredTags = new ArrayList<>();
                for (String tag : preloadSettings.tags()) {
                    if (!TagsUtils.checkTagPattern(tag)) {
                        InAppStoryManager.showELog(
                                InAppStoryManager.IAS_WARN_TAG,
                                StringsUtils.getFormattedErrorStringFromContext(
                                        core.appContext(),
                                        R.string.ias_tag_pattern_error,
                                        tag
                                )
                        );
                        continue;
                    }
                    filteredTags.add(tag);
                }
                if (StringsUtils.getBytesLength(TextUtils.join(",", filteredTags)) > TAG_LIMIT) {
                    InAppStoryManager.showELog(
                            InAppStoryManager.IAS_ERROR_TAG,
                            StringsUtils.getErrorStringFromContext(
                                    core.appContext(),
                                    R.string.ias_setter_tags_length_error
                            )
                    );
                    if (callback != null)
                        callback.loadError();
                    return;
                }
            }

        }

        new InAppMessagesUseCase(core, iamIds, filteredTags)
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
                        if (callback != null)
                            callback.loadError();
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
