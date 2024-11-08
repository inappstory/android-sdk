package com.inappstory.sdk.core.network.content.usecase;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.IContentHolder;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.callbacks.SimpleApiCallback;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.StoryListType;
import com.inappstory.sdk.core.network.content.callbacks.LoadFavoritesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.LoadFeedCallback;
import com.inappstory.sdk.core.network.content.callbacks.LoadStoriesCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.api.models.callbacks.SimpleListCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;
import com.inappstory.sdk.utils.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeedByIdUseCase {
    private final IASCore core;
    private final String feed;

    public FeedByIdUseCase(IASCore core, String feed) {
        this.core = core;
        this.feed = feed;
    }

    public void get(
            final LoadStoriesCallback callback,
            final LoadFavoritesCallback favCallback,
            final boolean hasFavorite
    ) {
        SimpleListCallback loadCallback = new SimpleListCallback() {
            @Override
            public void onSuccess(final List<Story> stories, Object... args) {

                String feedId = null;
                final ContentType type = ContentType.STORY;
                final IContentHolder contentHolder = core.contentHolder();
                for (int i = 0; i < stories.size(); i++) {
                    IListItemContent listItemContent = stories.get(i);
                    contentHolder.listsContent().setByIdAndType(
                            listItemContent, listItemContent.id(), type
                    );
                    contentHolder.like(listItemContent.id(), type, listItemContent.like());
                    contentHolder.favorite(listItemContent.id(), type, listItemContent.favorite());
                }
                core.storyListCache().saveStoriesOpened(type);
                boolean loadFav = hasFavorite;
                if (args != null && args.length > 0) {
                    loadFav &= (boolean) args[0];
                    if (args.length > 1) {
                        feedId = (String) args[1];
                    }
                }
                final String sFeedId = feedId;
                if (loadFav) {
                    final String loadFavUID = core.statistic().profiling().addTask("api_favorite_item");
                    loadStoryFavoriteList(new NetworkCallback<List<Story>>() {
                        @Override
                        public void onSuccess(List<Story> favorites) {
                            core.statistic().profiling().setReady(loadFavUID);
                            contentHolder.clearAllFavorites(type);
                            for (int i = 0; i < favorites.size(); i++) {
                                IListItemContent listItemContent = favorites.get(i);
                                /*contentHolder.listsContent().setByIdAndType(
                                        listItemContent, listItemContent.id(), type
                                );*/
                              //  contentHolder.like(listItemContent.id(), type, listItemContent.like());
                                contentHolder.favoriteItems().setByIdAndType(
                                        new StoryFavoriteImage(
                                                listItemContent.id(),
                                                listItemContent.imageCoverByQuality(Image.QUALITY_MEDIUM),
                                                listItemContent.backgroundColor()
                                        ),
                                        listItemContent.id(),
                                        ContentType.STORY
                                );
                                contentHolder.favorite(listItemContent.id(), type, true);
                            }
                            core.storyListCache().saveStoriesOpened(type);
                            invokeStoriesCallback(stories, callback);
                            if (favorites.size() > 0 && favCallback != null) {
                                favCallback.success(
                                        contentHolder
                                                .favoriteItems()
                                                .getByType(ContentType.STORY)
                                );
                            }
                        }

                        @Override
                        public Type getType() {
                            return new StoryListType();
                        }

                        @Override
                        public void errorDefault(String message) {
                            core.statistic().profiling().setReady(loadFavUID);
                            invokeStoriesCallback(stories, callback);
                        }
                    });
                } else {
                    invokeStoriesCallback(stories, callback);
                }
            }

            @Override
            public void onError(String message) {
                if (callback != null) {
                    callback.onError();
                }
            }
        };
        loadFeed(feed, loadCallback, true);
    }

    private void invokeStoriesCallback(List<Story> stories, LoadStoriesCallback callback) {
        if (callback != null) {
            List<Integer> ids = new ArrayList<>();
            for (Story story : stories) {
                if (story == null) continue;
                ids.add(story.id);
            }
            callback.storiesLoaded(ids);
        }
    }

    private void loadStoryFavoriteList(
            final NetworkCallback<List<Story>> callback
    ) {
        core.network().enqueue(
                core.network().getApi().getStories(
                        ApiSettings.getInstance().getTestKey(),
                        1,
                        null,
                        "id, background_color, image",
                        null
                ),
                callback
        );
    }

    private void loadFeedError(final String feed) {
        core.callbacksAPI().useCallback(
                IASCallbackType.ERROR,
                new UseIASCallback<ErrorCallback>() {
                    @Override
                    public void use(@NonNull ErrorCallback callback) {
                        callback.loadListError(StringsUtils.getNonNull(feed));
                    }
                }
        );
    }

    private void loadFeed(
            final String feed,
            final SimpleApiCallback<List<Story>> callback,
            final boolean retry
    ) {
        new ConnectionCheck().check(
                core.appContext(),
                new ConnectionCheckCallback(core) {
                    @Override
                    public void success() {
                        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
                            @Override
                            public void onSuccess(final String sessionId) {
                                final String loadStoriesUID =
                                        core.statistic().profiling().addTask("api_story_list");
                                core.network().enqueue(
                                        core.network().getApi().getFeed(
                                                feed,
                                                ApiSettings.getInstance().getTestKey(),
                                                0,
                                                TextUtils.join(",",
                                                        ((IASDataSettingsHolder) core.settingsAPI()).tags()),
                                                null,
                                                "stories.slides"
                                        ),
                                        new LoadFeedCallback() {
                                            @Override
                                            public void onSuccess(Feed response) {
                                                if (response == null) {
                                                    loadFeedError(feed);
                                                    callback.onError("");
                                                } else {
                                                    core.statistic().profiling().setReady(loadStoriesUID);
                                                    callback.onSuccess(
                                                            response.stories,
                                                            response.hasFavorite(),
                                                            response.getFeedId()
                                                    );
                                                }
                                            }

                                            @Override
                                            public void errorDefault(String message) {
                                                core.statistic().profiling().setReady(loadStoriesUID);
                                                loadFeedError(feed);
                                                callback.onError(message);
                                            }


                                            @Override
                                            public void error424(String message) {
                                                core.statistic().profiling().setReady(loadStoriesUID);
                                                loadFeedError(null);
                                                callback.onError(message);

                                                IASDataSettingsHolder dataSettingsHolder =
                                                        (IASDataSettingsHolder) core.settingsAPI();
                                                core.sessionManager().closeSession(
                                                        true,
                                                        false,
                                                        dataSettingsHolder.lang(),
                                                        dataSettingsHolder.userId(),
                                                        sessionId
                                                );

                                                if (retry)
                                                    loadFeed(feed, callback, false);
                                            }
                                        });
                            }

                            @Override
                            public void onError() {
                                loadFeedError(feed);
                                callback.onError("");
                            }
                        });
                    }

                    @Override
                    protected void error() {
                        loadFeedError(feed);
                        callback.onError("");
                    }
                }
        );

    }

}
