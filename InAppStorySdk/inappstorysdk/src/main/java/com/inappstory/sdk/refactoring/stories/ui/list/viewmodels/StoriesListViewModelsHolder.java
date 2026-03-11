package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import java.util.HashMap;
import java.util.Map;

public class StoriesListViewModelsHolder {
    private final Map<String, StoriesListItemViewModel> listItemsViewModels = new HashMap<>();
    private final Map<String, StoriesListViewModel> listViewModels = new HashMap<>();
    private final StoriesListFavoriteCellViewModel favoriteCellViewModel =
            new StoriesListFavoriteCellViewModel();

    private final Object lock = new Object();

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

    public StoriesListViewModel getOrCreateStoriesListViewModel(
            String key,
            StoriesListViewModelCreator creator
    ) {
        synchronized (lock) {
            StoriesListViewModel storiesListViewModel = listViewModels.get(key);
            if (storiesListViewModel == null) {
                storiesListViewModel = creator.create();
                listViewModels.put(key, storiesListViewModel);
            }
            return storiesListViewModel;
        }
    }
}
