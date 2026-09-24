package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT_COUNT;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASSingleStory;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.launcher.ILaunchScreenCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenStrategy;
import com.inappstory.sdk.core.network.content.usecase.StoryByStringIdUseCase;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IASSingleStoryImpl implements IASSingleStory {
    private final IASCore core;

    public IASSingleStoryImpl(IASCore core) {
        this.core = core;
    }

    private List<String> filteredTags(List<String> tags, IASDataSettingsHolder settingsHolder)
            throws TagsLengthException {
        final List<String> localTags;
        if (tags != null) {
            List<String> filteredList = new ArrayList<>();
            List<String> copyTags = new ArrayList<>(tags);
            for (String tag : copyTags) {
                if (!TagsUtils.checkTagPattern(tag)) {
                    InAppStoryManager.showELog(
                            LoggerTags.IAS_WARN_TAG,
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
            if (filteredList.size() > TAG_LIMIT_COUNT) {
                InAppStoryManager.showELog(
                        LoggerTags.IAS_ERROR_TAG,
                        StringsUtils.getErrorStringFromContext(
                                core.appContext(),
                                R.string.ias_setter_tags_count_error
                        )
                );
                throw new TagsLengthException();
            }
            localTags = filteredList;
        } else {
            localTags = settingsHolder.tags();
        }
        return localTags;
    }

    @Override
    public void showOnce(
            final CancellationTokenWithStatus cancellationToken,
            final Context context,
            final String storyId,
            boolean useTargeting,
            final List<String> tags,
            final AppearanceManager appearanceManager,
            final IShowStoryOnceCallback callback
    ) {

        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Incorrect user id and device id"
            );
            if (callback != null)
                callback.onError();
            return;
        }
        Set<String> opens = core.sharedPreferencesAPI().getStringSet(
                core.storyListCache().getLocalOpensKey(
                        ContentType.STORY
                )
        );
        if (opens != null && opens.contains(storyId) && callback != null) {
            if (cancellationToken != null && cancellationToken.cancelled()) return;
            callback.alreadyShown();
            return;
        }
        TargetingBodyObject targetingBodyObject = null;
        if (useTargeting) {
            List<String> filteredTags = null;
            try {
                filteredTags = filteredTags(tags, settingsHolder);
            } catch (TagsLengthException e) {
                if (callback != null)
                    callback.onError();
                return;
            }

            if (!settingsHolder.options().isEmpty() || (filteredTags != null && !filteredTags.isEmpty())) {
                targetingBodyObject = new TargetingBodyObject(filteredTags, settingsHolder.options());
            } else {
                targetingBodyObject = new TargetingBodyObject();
            }
        }
        new StoryByStringIdUseCase(core).get(
                storyId,
                targetingBodyObject,
                new GetStoryByIdCallback() {
                    @Override
                    public void getStory(final Story story, final String sessionId) {
                        if (story != null) {
                            core.contentHolder().readerContent().setByIdAndType(story, story.id(), ContentType.STORY);
                            core.contentLoader().storyDownloadManager().addCompletedStoryTask(story, ContentType.STORY);
                            openStoryInReader(
                                    cancellationToken,
                                    story,
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
                        if (cancellationToken != null && cancellationToken.cancelled()) return;
                        if (type == -2) {
                            if (callback != null) callback.alreadyShown();
                        } else {
                            if (callback != null) callback.onError();
                        }
                    }

                },
                true,
                SourceType.SINGLE
        );
    }

    private void internalShow(
            final CancellationTokenWithStatus cancellationToken,
            final Context context,
            final String storyId,
            final boolean useTargeting,
            final List<String> tags,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final Integer slide,
            final boolean fromReader,
            final SourceType readerSource,
            final int readerAction
    ) {
        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        if (((IASDataSettingsHolder) core.settingsAPI()).noCorrectUserIdOrDevice()) return;

        TargetingBodyObject targetingBodyObject = null;
        if (useTargeting) {
            List<String> filteredTags = null;
            try {
                filteredTags = filteredTags(tags, settingsHolder);
            } catch (TagsLengthException e) {
                if (callback != null)
                    callback.onError();
                return;
            }

            if (!settingsHolder.options().isEmpty() || (filteredTags != null && !filteredTags.isEmpty())) {
                targetingBodyObject = new TargetingBodyObject(filteredTags, settingsHolder.options());
            } else {
                targetingBodyObject = new TargetingBodyObject();
            }
        }
        new StoryByStringIdUseCase(core).get(
                storyId,
                targetingBodyObject,
                new GetStoryByIdCallback() {
                    @Override
                    public void getStory(final Story story, final String sessionId) {
                        if (story != null) {
                            core.contentHolder().readerContent().setByIdAndType(
                                    story,
                                    story.id(),
                                    ContentType.STORY
                            );
                            core.contentLoader().storyDownloadManager().addCompletedStoryTask(
                                    story,
                                    ContentType.STORY
                            );
                            openStoryInReader(
                                    cancellationToken,
                                    story,
                                    sessionId,
                                    context,
                                    appearanceManager,
                                    callback,
                                    slide,
                                    ContentType.STORY,
                                    readerSource,
                                    readerAction,
                                    fromReader
                            );
                        } else {
                            if (cancellationToken != null && cancellationToken.cancelled()) return;
                            if (callback != null) callback.onError();
                        }
                    }

                    @Override
                    public void loadError(int type) {
                        if (cancellationToken != null && cancellationToken.cancelled()) return;
                        if (callback != null) callback.onError();
                    }

                },
                false,
                readerSource
        );
    }

    public void externalShow(
            final CancellationTokenWithStatus cancellationToken,
            final Context context,
            final String storyId,
            final AppearanceManager appearanceManager,
            final IShowStoryCallback callback,
            final Integer slide,
            final boolean fromReader,
            final SourceType readerSource,
            final int readerAction
    ) {
        internalShow(
                cancellationToken,
                context,
                storyId,
                false,
                null,
                appearanceManager,
                callback,
                slide,
                fromReader,
                readerSource,
                readerAction
        );
    }


    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            Context context,
            String storyId,
            boolean useTargeting,
            final List<String> tags,
            AppearanceManager appearanceManager,
            IShowStoryCallback callback,
            Integer slide
    ) {
        IASDataSettingsHolder settingsHolder = ((IASDataSettingsHolder) core.settingsAPI());
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Incorrect user id and device id"
            );
            if (callback != null)
                callback.onError();
            return;
        }
        internalShow(
                cancellationToken,
                context,
                storyId,
                useTargeting,
                tags,
                appearanceManager,
                callback,
                slide,
                false,
                SourceType.SINGLE,
                ShowStory.ACTION_OPEN
        );
    }

    private void openStoryInReader(
            final CancellationTokenWithStatus cancellationToken,
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
        LaunchStoryScreenData launchData = new LaunchStoryScreenData()
                .sessionId(sessionId)
                .storiesIds(new ArrayList<>(stIds))
                .startFromFullscreenStory(story.fullscreen())
                .firstAction(readerAction)
                .sourceType(readerSource)
                .slideIndex(slide)
                .type(type)
                .cancellationTokenUID(cancellationToken.getUniqueId());
        boolean nonAnonymous = !((IASDataSettingsHolder) core.settingsAPI()).anonymous();

        core.screensManager().openScreen(context,
                new LaunchStoryScreenStrategy(core, openedFromReader)
                        .launchStoryScreenData(launchData)
                        .cancellationToken(cancellationToken)
                        .readerAppearanceSettings(
                                new LaunchStoryScreenAppearance(
                                        AppearanceManager.checkOrCreateAppearanceManager(manager),
                                        context,
                                        nonAnonymous
                                )
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
