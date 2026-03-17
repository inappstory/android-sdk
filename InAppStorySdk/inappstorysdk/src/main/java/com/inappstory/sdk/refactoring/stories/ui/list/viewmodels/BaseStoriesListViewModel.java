package com.inappstory.sdk.refactoring.stories.ui.list.viewmodels;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.refactoring.core.utils.observers.Observable;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoriesListState;
import com.inappstory.sdk.refactoring.stories.usecases.StoriesFeedParameters;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV2Impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseStoriesListViewModel {
    protected final IASCore core;
    protected final StoriesFeedParameters feedParameters;
    protected final boolean isFavorite;
    protected final ExecutorService scope = Executors.newSingleThreadExecutor();

    public void clear() {
        storiesListStateObservable.updateValue(new StoriesListState());
    }

    protected final Observable<StoriesListState> storiesListStateObservable =
            new Observable<>(null);

    public abstract void loadStories();

    public BaseStoriesListViewModel(IASCore core, StoriesFeedParameters feedParameters, boolean isFavorite) {
        this.core = core;
        this.isFavorite = isFavorite;
        this.feedParameters = feedParameters;
    }

    public void sendIndexes(List<Integer> indexes) {
        final List<Integer> newIndexes = core.statistic().newStatisticPreviews(indexes);
        if (newIndexes.isEmpty()) return;
        core.sessionManager().useOrOpenSession(
                new OpenSessionCallback() {
                    @Override
                    public void onSuccess(RequestLocalParameters requestLocalParameters) {
                        core.statistic().storiesV2().sendViewStory(
                                newIndexes,
                                isFavorite ?
                                        IASStatisticStoriesV2Impl.FAVORITE :
                                        IASStatisticStoriesV2Impl.LIST,
                                feedParameters.feed()
                        );
                        core.statistic().storiesV1(
                                requestLocalParameters.sessionId(),
                                new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                                        manager.previewStatisticEvent(newIndexes);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }

    public void addSubscriber(Observer<StoriesListState> observer) {
        this.storiesListStateObservable.subscribe(observer);
    }

    public void removeSubscriber(Observer<StoriesListState> observer) {
        this.storiesListStateObservable.unsubscribe(observer);
    }
}
