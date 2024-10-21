package com.inappstory.sdk.core.api.impl;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;

import java.util.ArrayList;
import java.util.Set;

public class IASSingleStoryImpl implements IASSingleStory {
    private final IASCore core;

    public IASSingleStoryImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void showOnce(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryOnceCallback callback
    ) {

        if (((IASDataSettingsHolder) core.settingsAPI()).noCorrectUserIdOrDevice()) return;
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(
                core.storyListCache().getLocalOpensKey(
                        ContentType.STORY
                )
        );
        if (opens != null && opens.contains(storyId) && callback != null) {
            callback.alreadyShown();
            return;
        }

        core.contentLoader().storyDownloadManager().getFullStoryByStringId(new GetStoryByIdCallback() {
            @Override
            public void getStory(final Story story, final String sessionId) {
                if (story != null) {
                    core.contentLoader().storyDownloadManager().addCompletedStoryTask(story, ContentType.STORY);
                    openStoryInReader(story,
                            sessionId,
                            context,
                            appearanceManager,
                            callback,
                            0,
                            ContentType.STORY,
                            SourceType.SINGLE,
                            ShowStory.ACTION_OPEN,
                            false
                    );
                } else {
                    if (callback != null) callback.onError();
                }
            }

            @Override
            public void loadError(int type) {
                if (type == -2) {
                    if (callback != null) callback.alreadyShown();
                } else {
                    if (callback != null) callback.onError();
                }
            }

        }, storyId, ContentType.STORY, true, SourceType.SINGLE);
    }


    public void show(
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final ContentType type,
            final Integer slide,
            final boolean fromReader,
            final SourceType readerSource,
            final int readerAction
    ) {
        if (((IASDataSettingsHolder) core.settingsAPI()).noCorrectUserIdOrDevice()) return;
        core.contentLoader().storyDownloadManager().getFullStoryByStringId(
                new GetStoryByIdCallback() {
                    @Override
                    public void getStory(final Story story, final String sessionId) {
                        if (story != null) {
                            core.contentLoader().storyDownloadManager().addCompletedStoryTask(story, type);
                            openStoryInReader(
                                    story,
                                    sessionId,
                                    context,
                                    appearanceManager,
                                    callback,
                                    slide,
                                    type,
                                    readerSource,
                                    readerAction,
                                    fromReader
                            );
                        } else {
                            if (callback != null) callback.onError();
                        }
                    }

                    @Override
                    public void loadError(int type) {
                        if (callback != null) callback.onError();
                    }

                },
                storyId,
                type,
                false,
                readerSource
        );
    }


    @Override
    public void show(
            Context context,
            String storyId,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide
    ) {
        show(
                context,
                storyId,
                appearanceManager,
                callback,
                ContentType.STORY,
                slide,
                false,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    private void openStoryInReader(
            final Story story,
            final String sessionId,
            final Context context,
            final AppearanceManager manager,
            final IShowStoryCallback callback,
            final Integer slide,
            final ContentType type,
            final SourceType readerSource,
            final int readerAction,
            final boolean openedFromReader
    ) {

        ArrayList<Integer> stIds = new ArrayList<>();
        stIds.add(story.id);
        LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                null,
                null,
                sessionId,
                stIds,
                0,
                false,
                readerAction,
                readerSource,
                slide,
                type,
                null
        );
        core.screensManager().openScreen(context,
                new LaunchStoryScreenStrategy(core, openedFromReader)
                        .launchStoryScreenData(launchData)
                        .readerAppearanceSettings(
                                new LaunchStoryScreenAppearance(
                                        AppearanceManager.checkOrCreateAppearanceManager(manager),
                                        context)
                        )
                        .addLaunchScreenCallback(
                                new ILaunchScreenCallback() {
                                    @Override
                                    public void onSuccess(ScreenType type) {
                                        if (callback != null) callback.onShow();
                                    }

                                    @Override
                                    public void onError(ScreenType type, String message) {
                                        if (callback != null) callback.onError();
                                    }
                                }
                        )
        );
    }


    @Override
    public void loadCallback(SingleLoadCallback singleLoadCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.SINGLE, singleLoadCallback);
    }
}
