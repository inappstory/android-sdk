package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.IStoriesFavoriteFeedChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.usecases.GetFavoriteStories;

import java.util.ArrayList;
import java.util.List;

public final class StoriesFavoriteListViewModel extends BaseStoriesListViewModel
        implements IStoriesFavoriteFeedChangeSubscriber {

    public StoriesFavoriteListViewModel(IASCore core) {
        super(core, null, true);
        core.storyChangesSubscribers().addFavoriteFeedChangeSubscriber(this);
    }

    @Override
    public void clear() {
        super.clear();
        core.storyChangesSubscribers().removeFavoriteFeedChangeSubscriber(this);
    }

    @Override
    public void onChange(String storyId, boolean add) {
        StoriesListState listState = storiesListStateObservable.getValue();
        if (listState != null) {
            List<String> ids = listState.storiesIds();
            if (ids == null) ids = new ArrayList<>();
            if (add) {
                if (ids.contains(storyId)) return;
                ids.add(0, storyId);
            } else {
                if (!ids.contains(storyId)) return;
                ids.remove(storyId);
            }
            storiesListStateObservable.updateValue(
                    new StoriesListState()
                            .storiesIds(ids)
                            .hasFavorite(false)
            );
        }
    }

    @Override
    public void loadStories() {
        scope.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        new GetFavoriteStories(
                                core.storyRepository()
                        ).invoke(
                                new ResultCallback<List<StoriesListItemDTO>>() {
                                    @Override
                                    public void success(List<StoriesListItemDTO> result) {
                                        List<String> ids = new ArrayList<>();
                                        for (StoriesListItemDTO listItemDTO : result) {
                                            ids.add(Integer.toString(listItemDTO.id()));
                                        }
                                        storiesListStateObservable.updateValue(
                                                new StoriesListState()
                                                        .storiesIds(ids)
                                                        .hasFavorite(false)
                                        );
                                    }

                                    @Override
                                    public void error(Error<List<StoriesListItemDTO>> result) {
                                        storiesListStateObservable.updateValue(
                                                new StoriesListState()
                                                        .hasFavorite(false)
                                        );
                                    }
                                }
                        );
                    }
                }
        );

    }
}
