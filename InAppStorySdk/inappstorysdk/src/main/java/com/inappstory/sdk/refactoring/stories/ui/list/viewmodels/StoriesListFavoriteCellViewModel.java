package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.IStoriesListFavoriteCellChangeSubscriber;
import com.inappstory.sdk.refactoring.stories.data.local.StoryCoverDTO;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellItemState;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListFavoriteCellState;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoriesListFavoriteCellViewModel implements IStoriesListFavoriteCellChangeSubscriber {
    private final Observable<StoriesListFavoriteCellState> storiesListItemStateObservable =
            new Observable<>(null);

    private Map<String, String> cachedImages = new HashMap<>();
    private final Object cacheLock = new Object();

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void loadCellItemImage(final int id, final String url) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                new StoryCoverUseCase(
                        core,
                        url,
                        new IGetStoryCoverCallback() {
                            @Override
                            public void success(final String file) {
                                synchronized (cacheLock) {
                                    cachedImages.put(url, file);
                                }
                                StoriesListFavoriteCellState newState =
                                        storiesListItemStateObservable.getValue().copy();
                                for (StoriesListFavoriteCellItemState itemState : newState.covers()) {
                                    if (itemState.id() == id) {
                                        itemState.filePath(file);
                                        break;
                                    }
                                }
                                storiesListItemStateObservable.updateValue(newState);
                            }

                            @Override
                            public void error() {

                            }
                        }
                ).getFile();
            }
        });

    }

    public void clear() {
        cachedImages.clear();
        storiesListItemStateObservable.updateValue(
                new StoriesListFavoriteCellState(new ArrayList<>())
        );
    }

    private final IASCore core;

    public StoriesListFavoriteCellViewModel(
            IASCore core
    ) {
        this.core = core;
        core.storyChangesSubscribers().addCoverCellChangeSubscriber(this);
    }

    public void addSubscriber(Observer<StoriesListFavoriteCellState> observer) {
        storiesListItemStateObservable.subscribeAndGetValue(observer);
    }

    public void removeSubscriber(Observer<StoriesListFavoriteCellState> observer) {
        storiesListItemStateObservable.unsubscribe(observer);
    }

    @Override
    public void onChange(List<StoryCoverDTO> covers) {
        List<StoriesListFavoriteCellItemState> itemStates = new ArrayList<>();
        for (StoryCoverDTO storyCoverDTO : covers) {
            StoriesListFavoriteCellItemState itemState =
                    new StoriesListFavoriteCellItemState()
                            .backgroundColor(
                                    storyCoverDTO.backgroundColor()
                            )
                            .id(storyCoverDTO.id());
            if (storyCoverDTO.imageUrl() != null && !storyCoverDTO.imageUrl().isEmpty()) {
                String path;
                synchronized (cacheLock) {
                    path = cachedImages.get(storyCoverDTO.imageUrl());
                }
                if (path != null)
                    itemState.filePath(path);
                else
                    loadCellItemImage(storyCoverDTO.id(), storyCoverDTO.imageUrl());
            }
            itemStates.add(itemState);
        }
        storiesListItemStateObservable.updateValue(new StoriesListFavoriteCellState(itemStates));
    }
}
