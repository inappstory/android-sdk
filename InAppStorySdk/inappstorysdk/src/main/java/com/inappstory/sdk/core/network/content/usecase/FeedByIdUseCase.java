package com.inappstory.sdk.core.network.content.usecase;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IFavoriteItem;
import com.inappstory.sdk.core.dataholders.IContentHolder;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.callbacks.SimpleApiCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
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
import com.inappstory.sdk.utils.format.StringsUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeedByIdUseCase {
    private final IASCore core;
    private final String feed;

    public FeedByIdUseCase(
            IASCore core,
            String feed
    ) {
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
                IASDataSettingsHolder dataSettingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                RequestLocalParameters requestLocalParameters = new RequestLocalParameters(
                        core.sessionManager().getSession().getSessionId(),
                        dataSettingsHolder.userId(),
                        dataSettingsHolder.lang()
                );
                if (args != null && args.length > 0) {
                    int shift = 0;
                    if (args[0] instanceof RequestLocalParameters) {
                        requestLocalParameters = (RequestLocalParameters) args[0];
                        shift = 1;
                    }
                    loadFav &= (boolean) args[shift];
                }
                if (loadFav) {
                    final String loadFavUID = core.statistic().profiling().addTask("api_favorite_item");
                    loadStoryFavoriteList(
                            new NetworkCallback<List<Story>>() {
                                @Override
                                public void onSuccess(List<Story> favorites) {
                                    core.statistic().profiling().setReady(loadFavUID);
                                    contentHolder.clearAllFavorites(type);
                                    List<IFavoriteItem> newFavItems = new ArrayList<>();
                                    for (int i = 0; i < favorites.size(); i++) {
                                        IListItemContent listItemContent = favorites.get(i);
                                        newFavItems.add(new StoryFavoriteImage(
                                                listItemContent.id(),
                                                listItemContent.imageCoverByQuality(Image.QUALITY_MEDIUM),
                                                listItemContent.backgroundColor()
                                        ));

                                        contentHolder.favorite(listItemContent.id(), type, true);
                                    }
                                    contentHolder.favoriteItems().setByType(
                                            newFavItems,
                                            ContentType.STORY
                                    );
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
                            },
                            requestLocalParameters
                    );
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
            NetworkCallback<List<Story>> callback,
            RequestLocalParameters requestLocalParameters
    ) {
        core.network().enqueue(
                core.network().getApi().getStories(
                        core.projectSettingsAPI().testKey(),
                        1,
                        null,
                        "id, background_color, image",
                        "slides",
                        requestLocalParameters.userId,
                        requestLocalParameters.sessionId,
                        requestLocalParameters.locale
                ),
                callback,
                requestLocalParameters
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
                        core.sessionManager().useOrOpenSession(
                                new OpenSessionCallback() {
                                    @Override
                                    public void onSuccess(final RequestLocalParameters requestLocalParameters) {
                                        final String loadStoriesUID =
                                                core.statistic().profiling().addTask("api_story_list");
                                        core.network().enqueue(
                                                core.network().getApi().getFeed(
                                                        feed,
                                                        core.projectSettingsAPI().testKey(),
                                                        0,
                                                        TextUtils.join(",",
                                                                ((IASDataSettingsHolder) core.settingsAPI()).tags()),
                                                        null,
                                                        "stories.slides",
                                                        requestLocalParameters.userId,
                                                        requestLocalParameters.sessionId,
                                                        requestLocalParameters.locale
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
                                                                    requestLocalParameters,
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
                                                        core.sessionManager().closeSession(
                                                                true,
                                                                false,
                                                                requestLocalParameters.locale,
                                                                requestLocalParameters.userId,
                                                                requestLocalParameters.sessionId
                                                        );

                                                        if (retry)
                                                            loadFeed(feed, callback, false);
                                                    }
                                                },
                                                requestLocalParameters
                                        );
                                    }

                                    @Override
                                    public void onError() {
                                        loadFeedError(feed);
                                        callback.onError("");
                                    }
                                }
                        );
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
