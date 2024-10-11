package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASFavorites;
import com.inappstory.sdk.core.ui.screens.holder.GetScreenCallback;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.lang.reflect.Type;

public class IASFavoritesImpl implements IASFavorites {
    private final IASCore core;

    public IASFavoritesImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void removeAll() {
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                favoriteRemoveAll();
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void removeByStoryId(final int storyId) {
        core.sessionManager().useOrOpenSession(new OpenSessionCallback() {
            @Override
            public void onSuccess(String sessionId) {
                favoriteOrRemoveStory(storyId, false);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void favoriteOrRemoveStory(final int storyId, final boolean favorite) {
        final String favUID = core.statistic().profiling().addTask("api_favorite");
        core.network().enqueue(
                core.network().getApi().storyFavorite(Integer.toString(storyId), favorite ? 1 : 0),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        core.statistic().profiling().setReady(favUID);
                        Story story = core.contentLoader().storyDownloadManager()
                                .getStoryById(storyId, Story.StoryType.COMMON);
                        if (story != null)
                            story.favorite = favorite;
                        core.inAppStoryService().getListReaderConnector().storyFavorite(storyId, favorite);
                        core
                                .screensManager()
                                .getStoryScreenHolder()
                                .useCurrentReader(
                                        new GetScreenCallback<BaseStoryScreen>() {
                                            @Override
                                            public void get(BaseStoryScreen screen) {
                                                screen.removeStoryFromFavorite(storyId);
                                            }
                                        }
                                );
                    }

                    @Override
                    public void onError(int code, String message) {
                        core.statistic().profiling().setReady(favUID);
                        super.onError(code, message);
                    }

                    @Override
                    public void timeoutError() {
                        super.timeoutError();
                        core.statistic().profiling().setReady(favUID);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });

    }

    private void favoriteRemoveAll() {
        final String favUID = core.statistic().profiling().addTask("api_favorite_remove_all");
        core.network().enqueue(
                core.network().getApi().removeAllFavorites(),
                new NetworkCallback<Response>() {
                    @Override
                    public void onSuccess(Response response) {
                        core.statistic().profiling().setReady(favUID);
                        core.contentLoader()
                                .storyDownloadManager()
                                .clearAllFavoriteStatus(
                                        Story.StoryType.COMMON
                                );
                        core.contentLoader()
                                .storyDownloadManager()
                                .clearAllFavoriteStatus(
                                        Story.StoryType.UGC
                                );
                        core.contentLoader().storyDownloadManager().favoriteImages().clear();
                        core.inAppStoryService().getListReaderConnector().clearAllFavorites();
                        core.screensManager().getStoryScreenHolder()
                                .useCurrentReader(
                                        new GetScreenCallback<BaseStoryScreen>() {
                                            @Override
                                            public void get(BaseStoryScreen screen) {
                                                screen.removeAllStoriesFromFavorite();
                                            }
                                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        core.statistic().profiling().setReady(favUID);
                        super.onError(code, message);
                    }

                    @Override
                    public void timeoutError() {
                        super.timeoutError();
                        core.statistic().profiling().setReady(favUID);
                    }

                    @Override
                    public Type getType() {
                        return null;
                    }
                });

    }
}
