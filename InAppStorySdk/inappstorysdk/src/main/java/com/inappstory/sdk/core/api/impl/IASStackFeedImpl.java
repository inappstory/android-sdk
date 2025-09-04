package com.inappstory.sdk.core.api.impl;

import android.content.Context;
import android.text.TextUtils;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.stackfeed.IStackFeedActions;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;
import com.inappstory.sdk.stories.stackfeed.IStackStoryData;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.stackfeed.StackStoryUpdatedCallback;
import com.inappstory.sdk.stories.utils.TagsUtils;
import com.inappstory.sdk.utils.StringsUtils;

import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

import java.util.ArrayList;
import java.util.List;

public class IASStackFeedImpl implements IASStackFeed {
    private final IASCore core;

    public IASStackFeedImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void get(
            String feed,
            String uniqueStackId,
            AppearanceManager appearanceManager,
            final List<String> tags,
            final IStackFeedResult stackFeedResult
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            stackFeedResult.error();
            return;
        }
        final String localTags;
        if (tags != null) {
            List<String> filteredList = new ArrayList<>();
            List<String> copyTags = new ArrayList<>(tags);
            for (String tag : copyTags) {
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
                stackFeedResult.error();
                return;

            }
            localTags = TextUtils.join(",", filteredList);
        } else {
            localTags = TextUtils.join(",", settingsHolder.tags());
        }
        final String localFeed;
        if (feed != null && !feed.isEmpty()) localFeed = feed;
        else localFeed = "default";
        final String localUniqueStackId = (uniqueStackId != null) ? uniqueStackId : localFeed;
        final AppearanceManager localAppearanceManager =
                appearanceManager != null ? appearanceManager
                        : AppearanceManager.getCommonInstance();
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(final RequestLocalParameters requestLocalParameters) {
                core.network().enqueue(
                        core.network().getApi().getFeed(
                                localFeed,
                                core.projectSettingsAPI().testKey(),
                                0,
                                localTags,
                                null,
                                "stories.slides",
                                requestLocalParameters.userId(),
                                requestLocalParameters.sessionId(),
                                requestLocalParameters.locale()
                        ),
                        new LoadFeedCallback() {
                            @Override
                            public void onSuccess(final Feed response) {
                                if (response == null || response.stories == null) {
                                    stackFeedResult.error();
                                } else {
                                    for (IListItemContent story: response.stories) {
                                        core.contentHolder().listsContent().setByIdAndType(
                                                story, story.id(), ContentType.STORY
                                        );
                                    }
                                    core.storyListCache().saveStoriesOpened(ContentType.STORY);
                                    final StackStoryObserver observer = new StackStoryObserver(
                                            core,
                                            response.stories,
                                            requestLocalParameters.sessionId(),
                                            localAppearanceManager,
                                            localUniqueStackId,
                                            localFeed,
                                            new StackStoryUpdatedCallback() {
                                                @Override
                                                public void onUpdate(IStackStoryData newStackStoryData) {
                                                    stackFeedResult.update(newStackStoryData);
                                                }
                                            }
                                    );

                                    final IStackFeedActions stackFeedActions = new IStackFeedActions() {
                                        @Override
                                        public void openReader(Context context) {
                                            observer.openReader(context);
                                        }

                                        @Override
                                        public void unsubscribe() {
                                            observer.unsubscribe();
                                        }
                                    };
                                    if (response.stories.size() == 0) {
                                        stackFeedResult.success(null, stackFeedActions);
                                        return;
                                    }
                                    final Runnable loadObserver = new Runnable() {
                                        @Override
                                        public void run() {
                                            observer.subscribe();
                                            observer.onLoad(new StackStoryUpdatedCallback() {
                                                @Override
                                                public void onUpdate(IStackStoryData newStackStoryData) {
                                                    stackFeedResult.success(
                                                            newStackStoryData,
                                                            stackFeedActions
                                                    );
                                                }
                                            });
                                        }
                                    };
                                    Image feedCover = response.getProperCover(localAppearanceManager.csCoverQuality());
                                    if (feedCover != null) {
                                        observer.feedCover = feedCover.getUrl();
                                    }
                                    loadObserver.run();
                                }
                            }

                            @Override
                            public void onError(int code, String message) {
                                stackFeedResult.error();
                            }
                        },
                        requestLocalParameters
                );
            }

            @Override
            public void onError() {
                stackFeedResult.error();
            }
        });
    }
}
