package com.inappstory.sdk.core.api.impl;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.stories.api.models.Feed;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.stackfeed.IStackFeedActions;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;
import com.inappstory.sdk.stories.stackfeed.IStackStoryData;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.stackfeed.StackStoryUpdatedCallback;
import static com.inappstory.sdk.core.api.impl.IASSettingsImpl.TAG_LIMIT;

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
        final NetworkClient networkClient = InAppStoryManager.getNetworkClient();
        if (networkClient == null) {
            stackFeedResult.error();
            return;
        }
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.noCorrectUserIdOrDevice()) {
            stackFeedResult.error();
            return;
        }
        final String localTags;
        if (tags != null) {
            localTags = TextUtils.join(",", tags);
        } else {
            localTags = TextUtils.join(",", settingsHolder.tags());
        }
        if (localTags.length() > TAG_LIMIT) {
            stackFeedResult.error();
            return;
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
            public void onSuccess(final String sessionId) {
                networkClient.enqueue(
                        networkClient.getApi().getFeed(
                                localFeed,
                                ApiSettings.getInstance().getTestKey(),
                                0,
                                localTags,
                                null,
                                null
                        ),
                        new LoadFeedCallback() {
                            @Override
                            public void onSuccess(final Feed response) {
                                if (response == null || response.stories == null) {
                                    stackFeedResult.error();
                                } else {
                                    core.storyListCache().saveStoriesOpened(response.stories, Story.StoryType.COMMON);
                                    core.contentLoader().storyDownloadManager().uploadingAdditional(
                                            response.stories,
                                            Story.StoryType.COMMON
                                    );
                                    final StackStoryObserver observer = new StackStoryObserver(
                                            core,
                                            response.stories,
                                            sessionId,
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
                        }
                );
            }

            @Override
            public void onError() {
                stackFeedResult.error();
            }
        });
    }
}
