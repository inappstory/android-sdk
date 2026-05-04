package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.IStoriesFavoriteFeedChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.usecases.GetFavoriteStories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StoriesFavoriteListViewModel extends BaseStoriesListViewModel
        implements IStoriesFavoriteFeedChangeSubscriber {

    public StoriesFavoriteListViewModel(IASCore core) {
        super(core,  true);
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
            Map<String, Boolean> hir = new HashMap<>(listState.hideInReader());
            hir.remove(storyId);
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
                            .hideInReader(hir)
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
                                        Map<String, Boolean> hir = new HashMap<>();
                                        for (StoriesListItemDTO listItemDTO : result) {
                                            ids.add(Integer.toString(listItemDTO.id()));
                                            hir.put(
                                                    Integer.toString(listItemDTO.id()),
                                                    listItemDTO.hideInReader()
                                            );
                                        }
                                        storiesListStateObservable.updateValue(
                                                new StoriesListState()
                                                        .hideInReader(hir)
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
