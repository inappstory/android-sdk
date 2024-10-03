package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.SharedPreferencesAPI;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IASOnboardingsImpl implements IASOnboardings {
    private final IASCore core;

    public IASOnboardingsImpl(IASCore core) {
        this.core = core;
    }

    private final static String ONBOARDING_FEED = "onboarding";

    @Override
    public void show(
            final Context context,
            String feed,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final int limit
    ) {
        if (feed == null || feed.isEmpty()) feed = ONBOARDING_FEED;
        final String usedFeed = feed;
        final IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            loadOnboardingError(usedFeed, "Incorrect user id and device id");
            return;
        }
        final String localTags;
        if (tags != null) {
            localTags = TextUtils.join(",", tags);
        } else {
            localTags = TextUtils.join(",", settingsHolder.tags());
        }
        if (localTags.length() > TAG_LIMIT) {
            loadOnboardingError(usedFeed, "Tags string too long");
            return;
        }
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) return;
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final String sessionId) {
                final String onboardUID =
                        ProfilingManager.getInstance().addTask("api_onboarding");
                networkClient.enqueue(
                        networkClient.getApi().getOnboardingFeed(
                                usedFeed,
                                ApiSettings.getInstance().getTestKey(),
                                limit,
                                localTags
                        ),
                        new LoadFeedCallback() {
                            @Override
                            public void onSuccess(Feed response) {
                                InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
                                if (inAppStoryManager == null) return;
                                ProfilingManager.getInstance().setReady(onboardUID);
                                List<Story> notOpened = new ArrayList<>();
                                String key = core.storyListCache().getLocalOpensKey(Story.StoryType.COMMON);
                                Set<String> opens = SharedPreferencesAPI.getStringSet(
                                        key
                                );
                                if (opens == null) opens = new HashSet<>();
                                if (response.stories != null) {
                                    for (Story story : response.stories) {
                                        boolean add = true;
                                        for (String opened : opens) {
                                            if (Integer.toString(story.id).equals(opened)) {
                                                add = false;
                                            }
                                        }
                                        if (add) notOpened.add(story);
                                    }
                                }
                                showLoadedOnboardings(notOpened, context, appearanceManager, sessionId, usedFeed);
                            }

                            @Override
                            public void onError(int code, String message) {
                                ProfilingManager.getInstance().setReady(onboardUID);
                                loadOnboardingError(usedFeed, "Can't load onboardings: request code " + code);
                            }

                            @Override
                            public void timeoutError() {
                                ProfilingManager.getInstance().setReady(onboardUID);
                                loadOnboardingError(usedFeed, "Can't load onboardings: timeout");
                            }
                        });
            }

            @Override
            public void onError() {
                loadOnboardingError(usedFeed, "Can't open session");
            }

        });
    }

    private void loadOnboardingError(final String feed, final String message) {
        core.callbacksAPI().useCallback(IASCallbackType.ONBOARDING,
                new UseIASCallback<OnboardingLoadCallback>() {
                    @Override
                    public void use(@NonNull OnboardingLoadCallback callback) {
                        callback.onboardingLoadError(
                                StringsUtils.getNonNull(feed),
                                message
                        );
                    }
                }
        );
    }

    private void showLoadedOnboardings(
            final List<Story> response,
            final Context outerContext,
            final AppearanceManager manager,
            final String sessionId,
            final String feed
    ) {
        Story.StoryType storyType = Story.StoryType.COMMON;
        if (response == null || response.size() == 0) {
            core.callbacksAPI().useCallback(IASCallbackType.ONBOARDING,
                    new UseIASCallback<OnboardingLoadCallback>() {
                        @Override
                        public void use(@NonNull OnboardingLoadCallback callback) {
                            callback.onboardingLoadSuccess(
                                    0,
                                    StringsUtils.getNonNull(feed)
                            );
                        }
                    }
            );
            return;
        }

        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) return;
        ArrayList<Story> stories = new ArrayList<Story>();
        ArrayList<Integer> storiesIds = new ArrayList<>();
        stories.addAll(response);
        for (Story story : response) {
            storiesIds.add(story.id);
        }
        inAppStoryService.getStoryDownloadManager().uploadingAdditional(stories, storyType);
        LaunchStoryScreenData launchData = new LaunchStoryScreenData(
                null,
                feed,
                sessionId,
                storiesIds,
                0,
                false,
                ShowStory.ACTION_OPEN,
                SourceType.ONBOARDING,
                0,
                Story.StoryType.COMMON,
                null
        );
        core.screensManager().openScreen(
                outerContext,
                new LaunchStoryScreenStrategy(false).
                        launchStoryScreenData(launchData).
                        readerAppearanceSettings(
                                new LaunchStoryScreenAppearance(
                                        AppearanceManager.checkOrCreateAppearanceManager(manager),
                                        outerContext
                                )
                        )
                        .addLaunchScreenCallback(
                                new ILaunchScreenCallback() {
                                    @Override
                                    public void onSuccess(ScreenType type) {
                                        core.callbacksAPI().useCallback(IASCallbackType.ONBOARDING,
                                                new UseIASCallback<OnboardingLoadCallback>() {
                                                    @Override
                                                    public void use(@NonNull OnboardingLoadCallback callback) {
                                                        callback.onboardingLoadSuccess(
                                                                response.size(),
                                                                StringsUtils.getNonNull(feed)
                                                        );
                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void onError(ScreenType type, final String message) {
                                        core.callbacksAPI().useCallback(IASCallbackType.ONBOARDING,
                                                new UseIASCallback<OnboardingLoadCallback>() {
                                                    @Override
                                                    public void use(@NonNull OnboardingLoadCallback callback) {
                                                        callback.onboardingLoadError(
                                                                StringsUtils.getNonNull(feed),
                                                                message
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                }
                        )
        );

    }

    @Override
    public void loadCallback(OnboardingLoadCallback onboardingLoadCallback) {
        core.callbacksAPI().setCallback(IASCallbackType.ONBOARDING, onboardingLoadCallback);
    }
}
