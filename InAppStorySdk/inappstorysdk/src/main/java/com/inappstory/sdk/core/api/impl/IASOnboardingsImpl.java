package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASOnboardings;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.TagsUtils;
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
            List<String> filteredList = new ArrayList<>();
            for (String tag : tags) {
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
                filteredList.add(tag);
            }
            if (StringsUtils.getBytesLength(TextUtils.join(",", filteredList)) > TAG_LIMIT) {
                InAppStoryManager.showELog(
                        InAppStoryManager.IAS_ERROR_TAG,
                        StringsUtils.getErrorStringFromContext(
                                core.appContext(),
                                R.string.ias_setter_tags_length_error
                        )
                );
                loadOnboardingError(usedFeed, "Tags string too long");
                return;

            }
            localTags = TextUtils.join(",", filteredList);
        } else {
            localTags = TextUtils.join(",", settingsHolder.tags());
        }
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final RequestLocalParameters requestLocalParameters) {
                final String onboardUID =
                        core.statistic().profiling().addTask("api_onboarding");
                core.network().enqueue(
                        core.network().getApi().getOnboardingFeed(
                                usedFeed,
                                ApiSettings.getInstance().getTestKey(),
                                limit,
                                localTags,
                                "stories.slides",
                                requestLocalParameters.userId,
                                requestLocalParameters.sessionId,
                                requestLocalParameters.locale
                        ),
                        new LoadFeedCallback() {
                            @Override
                            public void onSuccess(Feed response) {
                                InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
                                if (inAppStoryManager == null) return;
                                core.statistic().profiling().setReady(onboardUID);
                                List<Story> notOpened = new ArrayList<>();
                                String key = core.storyListCache().getLocalOpensKey(ContentType.STORY);
                                Set<String> opens = core.sharedPreferencesAPI().getStringSet(
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
                                showLoadedOnboardings(
                                        notOpened,
                                        context,
                                        appearanceManager,
                                        requestLocalParameters.sessionId,
                                        usedFeed
                                );
                            }

                            @Override
                            public void onError(int code, String message) {
                                core.statistic().profiling().setReady(onboardUID);
                                loadOnboardingError(usedFeed, "Can't load onboardings: request code " + code);
                            }

                            @Override
                            public void timeoutError() {
                                core.statistic().profiling().setReady(onboardUID);
                                loadOnboardingError(usedFeed, "Can't load onboardings: timeout");
                            }
                        },
                        requestLocalParameters
                );
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
        ContentType contentType = ContentType.STORY;
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
        ArrayList<Story> stories = new ArrayList<Story>();
        ArrayList<Integer> storiesIds = new ArrayList<>();
        stories.addAll(response);
        for (IListItemContent story : response) {
            storiesIds.add(story.id());
            core.contentHolder().listsContent().setByIdAndType(story, story.id(), contentType);
        }
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
                ContentType.STORY,
                null
        );
        core.screensManager().openScreen(
                outerContext,
                new LaunchStoryScreenStrategy(core, false).
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
