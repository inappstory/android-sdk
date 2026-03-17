package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryFeedDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.usecases.GetFavoriteCovers;
import com.inappstory.sdk.refactoring.stories.usecases.GetStoriesFeed;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;

import java.util.List;


public final class StoriesFeedListViewModel extends BaseStoriesListViewModel {
    private final IASCore core;
    private final StoriesFeedParameters feedParameters;

    public StoriesFeedListViewModel(IASCore core, StoriesFeedParameters feedParameters) {
        super(core, feedParameters, false);
        this.core = core;
        this.feedParameters = feedParameters;
    }

    private void loadFeed() {
        new GetStoriesFeed(
                core.sessionRepository(),
                core.storyRepository(),
                feedParameters
        ).invoke(
                new ResultCallback<StoryFeedDTO>() {
                    @Override
                    public void success(StoryFeedDTO result) {
                        StoriesListState state = storiesListStateObservable.getValue();
                        if (state == null)
                            state = new StoriesListState();
                        else
                            state = state.copy();
                        storiesListStateObservable.updateValue(
                                state.storiesIds(result.storiesIds())
                        );
                        if (result.hasFavorite()) {
                            loadCovers();
                        }
                    }

                    @Override
                    public void error(Error<StoryFeedDTO> result) {
                        storiesListStateObservable.updateValue(
                                new StoriesListState()
                        );
                    }
                }
        );
    }

    private void loadCovers() {
        new GetFavoriteCovers(
                core.sessionRepository(),
                core.storyRepository()
        ).invoke(
                new ResultCallback<List<StoryCoverDTO>>() {
                    @Override
                    public void success(List<StoryCoverDTO> result) {
                        StoriesListState state = storiesListStateObservable.getValue();
                        if (state == null)
                            state = new StoriesListState().hasFavorite(false);
                        else
                            state = state.copy().hasFavorite(!(result == null || result.isEmpty()));
                        storiesListStateObservable.updateValue(state);
                    }

                    @Override
                    public void error(Error<List<StoryCoverDTO>> result) {
                        StoriesListState state = storiesListStateObservable.getValue();
                        if (state == null)
                            state = new StoriesListState().hasFavorite(false);
                        else
                            state = state.copy().hasFavorite(false);
                        storiesListStateObservable.updateValue(state);
                    }
                }
        );
    }

    @Override
    public void loadStories() {
        scope.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        loadFeed();
                    }
                }
        );
    }
}
