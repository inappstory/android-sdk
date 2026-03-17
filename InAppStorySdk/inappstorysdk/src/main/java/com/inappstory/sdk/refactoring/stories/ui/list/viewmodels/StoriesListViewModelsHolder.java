package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;

import java.util.HashMap;
import java.util.Map;

public class StoriesListViewModelsHolder {
    private final IASCore core;

    private final Map<String, StoriesListItemViewModel> listItemsViewModels = new HashMap<>();
    private final Map<String, BaseStoriesListViewModel> listViewModels = new HashMap<>();
    private StoriesListFavoriteCellViewModel favoriteCellViewModel;

    public StoriesListFavoriteCellViewModel getOrCreateFavoriteCellViewModel() {
        if (favoriteCellViewModel == null)
            favoriteCellViewModel = new StoriesListFavoriteCellViewModel(core);
        return favoriteCellViewModel;
    }

    private final Object lock = new Object();

    public StoriesListViewModelsHolder(IASCore core) {
        this.core = core;
    }

    public StoriesListItemViewModel getOrCreateStoriesListItemViewModel(
            String key,
            StoriesListItemViewModelCreator creator
    ) {
        synchronized (lock) {
            StoriesListItemViewModel storiesListItemViewModel = listItemsViewModels.get(key);
            if (storiesListItemViewModel == null) {
                storiesListItemViewModel = creator.create();
                listItemsViewModels.put(key, storiesListItemViewModel);
            }
            return storiesListItemViewModel;
        }
    }

    public void clear() {
        listItemsViewModels.clear();
    }

    public BaseStoriesListViewModel getOrCreateStoriesListViewModel(
            String key,
            StoriesListViewModelCreator creator
    ) {
        synchronized (lock) {
            BaseStoriesListViewModel baseStoriesListViewModel = listViewModels.get(key);
            if (baseStoriesListViewModel == null) {
                baseStoriesListViewModel = creator.create();
                listViewModels.put(key, baseStoriesListViewModel);
            }
            return baseStoriesListViewModel;
        }
    }
}
