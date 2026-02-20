package com.inappstory.sdk.refactoring.stories.repositories.datasources;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.refactoring.core.utils.models.Error;
import com.inappstory.sdk.refactoring.core.utils.models.Error424;
import com.inappstory.sdk.refactoring.core.utils.models.Result;
import com.inappstory.sdk.refactoring.core.utils.models.ResultCallback;
import com.inappstory.sdk.refactoring.core.utils.models.Success;
import com.inappstory.sdk.refactoring.core.utils.models.TimeoutError;
import com.inappstory.sdk.refactoring.stories.data.network.NFeed;
import com.inappstory.sdk.refactoring.stories.data.network.NStory;
import com.inappstory.sdk.refactoring.stories.usecases.StoryFeedParameters;
import com.inappstory.sdk.refactoring.stories.data.network.NStoryListType;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.refactoring.stories.data.network.LoadNFeedCallback;

import java.lang.reflect.Type;
import java.util.List;

public class StoryAPIDataSource implements IStoryAPIDataSource {
    private final IASCore core;
    private final RequestLocalParameters requestLocalParameters;

    public StoryAPIDataSource(RequestLocalParameters requestLocalParameters, IASCore core) {
        this.core = core;
        this.requestLocalParameters = requestLocalParameters;
    }

    @Override
    public void getStoriesFeed(StoryFeedParameters feedParameters, ResultCallback<NFeed> storyFeedResultCallback) {
        core.network().enqueue(
                core.network().getApi().getFeed(
                        feedParameters.feed(),
                        core.projectSettingsAPI().testKey(),
                        0,
                        new TargetingBodyObject(
                                feedParameters.tags().isEmpty() ? null : feedParameters.tags(),
                                feedParameters.options()
                        ),
                        null,
                        "stories.slides",
                        requestLocalParameters.userId(),
                        requestLocalParameters.sessionId(),
                        requestLocalParameters.locale()
                ),
                new LoadNFeedCallback() {
                    @Override
                    public void onSuccess(NFeed response) {
                        storyFeedResultCallback.invoke(new Success<>(response));
                    }

                    @Override
                    public void errorDefault(String message) {
                        storyFeedResultCallback.invoke(new Error<>(message));
                    }

                    @Override
                    public void error424(String message) {
                        storyFeedResultCallback.invoke(new Error424<>(message));
                    }

                    @Override
                    public void timeoutError() {
                        storyFeedResultCallback.invoke(new TimeoutError<>(null));
                    }
                },
                requestLocalParameters
        );
    }

    @Override
    public void getFavoriteStories(ResultCallback<List<NStory>> storyFeedResultCallback) {
        core.network().enqueue(
                core.network().getApi().getStories(
                        core.projectSettingsAPI().testKey(),
                        1,
                        null,
                        null,
                        "slides",
                        requestLocalParameters.userId(),
                        requestLocalParameters.sessionId(),
                        requestLocalParameters.locale()
                ),
                new NetworkCallback<List<NStory>>() {
                    @Override
                    public void onSuccess(List<NStory> response) {
                        storyFeedResultCallback.invoke(new Success<>(response));
                    }

                    @Override
                    public Type getType() {
                        return new NStoryListType();
                    }

                    @Override
                    public void errorDefault(String message) {
                        storyFeedResultCallback.invoke(new Error<>(message));
                    }

                    @Override
                    public void error424(String message) {
                        storyFeedResultCallback.invoke(new Error424<>(message));
                    }

                    @Override
                    public void timeoutError() {
                        storyFeedResultCallback.invoke(new TimeoutError<>(null));
                    }
                },
                requestLocalParameters
        );
    }

    @Override
    public void getFavoriteCovers(ResultCallback<List<NStory>> storyFeedResultCallback) {
        core.network().enqueue(
                core.network().getApi().getStories(
                        core.projectSettingsAPI().testKey(),
                        1,
                        null,
                        "id, background_color, image",
                        null,
                        requestLocalParameters.userId(),
                        requestLocalParameters.sessionId(),
                        requestLocalParameters.locale()
                ),
                new NetworkCallback<List<NStory>>() {
                    @Override
                    public void onSuccess(List<NStory> response) {
                        storyFeedResultCallback.invoke(new Success<>(response));
                    }

                    @Override
                    public Type getType() {
                        return new NStoryListType();
                    }

                    @Override
                    public void errorDefault(String message) {
                        storyFeedResultCallback.invoke(new Error<>(message));
                    }

                    @Override
                    public void error424(String message) {
                        storyFeedResultCallback.invoke(new Error424<>(message));
                    }

                    @Override
                    public void timeoutError() {
                        storyFeedResultCallback.invoke(new TimeoutError<>(null));
                    }
                },
                requestLocalParameters
        );
    }

    @Override
    public void getOnboardingStoriesFeed(
            StoryFeedParameters feedParameters,
            int limit,
            ResultCallback<NFeed> storyFeedResultCallback
    ) {
        core.network().enqueue(
                core.network().getApi().getOnboardingFeed(
                        feedParameters.feed(),
                        core.projectSettingsAPI().testKey(),
                        limit,
                        new TargetingBodyObject(
                                feedParameters.tags().isEmpty() ? null : feedParameters.tags(),
                                feedParameters.options()
                        ),
                        "stories.slides",
                        requestLocalParameters.userId(),
                        requestLocalParameters.sessionId(),
                        requestLocalParameters.locale()
                ),
                new LoadNFeedCallback() {
                    @Override
                    public void onSuccess(NFeed response) {
                        storyFeedResultCallback.invoke(new Success<>(response));
                    }

                    @Override
                    public void errorDefault(String message) {
                        storyFeedResultCallback.invoke(new Error<>(message));
                    }

                    @Override
                    public void error424(String message) {
                        storyFeedResultCallback.invoke(new Error424<>(message));
                    }

                    @Override
                    public void timeoutError() {
                        storyFeedResultCallback.invoke(new TimeoutError<>(null));
                    }
                },
                requestLocalParameters
        );
    }

    @Override
    public void likeStory(String storyId, boolean like, ResultCallback<Boolean> likeResultCallback) {

    }

    @Override
    public void dislikeStory(String storyId, boolean dislike, ResultCallback<Boolean> dislikeResultCallback) {

    }

    @Override
    public void favoriteStory(String storyId, boolean favorite, ResultCallback<Boolean> favoriteResultCallback) {

    }

    @Override
    public void removeAllFavorites(ResultCallback<Void> removeAllFavoritesCallback) {

    }

    @Override
    public Result<NStory> getStoryBySlugOrId(String storySlugOrId) {
        return new Error<>(null);
    }
}
