package com.inappstory.sdk.core.banners;


import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.BannerPlaceLoadSettings;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BannerListViewModel implements IBannersWidgetViewModel<BannerListState> {
    private final Observable<BannerListState> bannerPlaceStateObservable =
            new Observable<>(new BannerListState());

    private final IASCore core;
    private String placeId;
    private final String uid = UUID.randomUUID().toString();

    public void tags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        updateState(new BannerListState().tags(tags));
    }

    private final List<String> tags = new ArrayList<>();
    private String uniqueId;
    BannerViewModelsHolder bannerViewModelsHolder;

    public BannerListViewModel(IASCore core, String uniqueId) {
        this.uniqueId = uniqueId;
        bannerViewModelsHolder = new BannerViewModelsHolder(core, this);
        this.core = core;
        addSubscriber(localObserver);
    }

    public void placeId(String placeId) {
        this.placeId = placeId;
        updateState(new BannerListState().placeId(placeId));
    }

    public String placeId() {
        return this.placeId;
    }


    public void uniqueId(String uniqueId) {
        if (uniqueId == null || uniqueId.isEmpty()) return;
        if (Objects.equals(this.uniqueId, uniqueId)) return;
        if (this.uniqueId != null) {
            core.widgetViewModels().bannerPlaceViewModels().changeKey(this.uniqueId, uniqueId);
        }
        this.uniqueId = uniqueId;
    }

    private final Set<InnerBannerPlaceLoadCallback> callbacks = new HashSet<>();

    private final Object callbacksLock = new Object();

    @Override
    public void addBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback) {
        synchronized (callbacksLock) {
            callbacks.add(callback);
        }
    }

    @Override
    public void removeBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback) {
        synchronized (callbacksLock) {
            callbacks.remove(callback);
        }
    }


    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    @Override
    public BannerListState getCurrentBannerPlaceState() {
        return bannerPlaceStateObservable.getValue();
    }

    Observer<BannerListState> localObserver = new Observer<BannerListState>() {
        BannersWidgetLoadStates bannerPlaceLoadState = BannersWidgetLoadStates.NONE;

        @Override
        public void onUpdate(BannerListState newValue) {
            if (newValue.loadState() == null || bannerPlaceLoadState.equals(newValue.loadState()))
                return;
            Set<InnerBannerPlaceLoadCallback> callbacks = new HashSet<>();
            List<IBanner> content = newValue.getItems();
            synchronized (callbacksLock) {
                callbacks.addAll(BannerListViewModel.this.callbacks);
            }
            bannerPlaceLoadState = newValue.loadState();
            switch (newValue.loadState()) {
                case FAILED:
                    for (InnerBannerPlaceLoadCallback callback : callbacks) {
                        try {
                            callback.loadError();
                        } catch (Exception e) {
                        }
                    }
                    break;
                case NONE:
                case LOADING:
                    break;
                case LOADED:
                case EMPTY:
                    for (InnerBannerPlaceLoadCallback callback : callbacks) {
                        try {
                            callback.bannerPlaceLoaded(
                                    content
                            );
                        } catch (Exception e) {
                        }
                    }
                    break;
            }

        }
    };

    @Override
    public void updateState(BannerListState bannerPlaceState) {
        bannerPlaceStateObservable.updateValue(bannerPlaceState);
    }

    @Override
    public void addSubscriber(Observer<BannerListState> observer) {
        this.bannerPlaceStateObservable.subscribeAndGetValue(observer);
    }

    @Override
    public void addSubscriberAndCheckLocal(Observer<BannerListState> observer) {
        this.bannerPlaceStateObservable.subscribeAndGetValueForced(observer);
    }

    @Override
    public void removeSubscriber(Observer<BannerListState> observer) {
        this.bannerPlaceStateObservable.unsubscribe(observer);
    }

    @Override
    public IBannerViewModel getBannerViewModel(int id, int index) {
        return bannerViewModelsHolder
                .get(
                        id,
                        index,
                        placeId
                );
    }

    @Override
    public void removeBannerViewModel(IBannerViewModel bannerViewModel) {
        bannerViewModelsHolder.removeViewModel(bannerViewModel);
    }

    @Override
    public List<IBannerViewModel> getBannerViewModels() {
        BannerListState placeState = bannerPlaceStateObservable.getValue();
        List<IBanner> items = placeState.items;
        List<IBannerViewModel> bannerViewModels = new ArrayList<>();
        if (items == null) return bannerViewModels;
        for (IBanner banner : items) {
            bannerViewModels.addAll(bannerViewModelsHolder.get(banner.id(), placeId));
        }
        return bannerViewModels;
    }

    @Override
    public void updateCurrentIndex(int index) {

    }

    @Override
    public void sendOpenStat(int bannerId, String iterationId) {
        ShownBannerKey key = new ShownBannerKey(iterationId, bannerId);
        if (shownBanners.contains(key)) return;
        shownBanners.add(key);
        core.statistic().bannersV1().sendOpenEvent(bannerId, 0, 1, iterationId);
    }

    Set<ShownBannerKey> shownBanners = new HashSet<>();

    @Override
    public void showNext() {

    }

    @Override
    public void clear() {
        BannerListState bannerPlaceState = bannerPlaceStateObservable.getValue();
        List<String> tags = null;
        String placeId = "";
        if (bannerPlaceState != null) {
            tags = bannerPlaceState.tags();
            placeId = bannerPlaceState.placeId();
        }
        bannerPlaceStateObservable.setValue(
                new BannerListState()
                        .placeId(placeId)
                        .tags(tags)
        );
        bannerViewModelsHolder.clearViewModels();
    }

    @Override
    public void clearBanners() {
        bannerViewModelsHolder.clearViewModels();
    }

    @Override
    public void reloadSubscriber() {
        List<Observer<BannerListState>> subscribers = bannerPlaceStateObservable.getSubscribers();
        boolean hasUISubs = false;
        for (Observer<BannerListState> subscriber : subscribers) {
            if (subscriber == localObserver) continue;
            hasUISubs = true;
        }
        clear();
        if (hasUISubs) {
            InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.bannersAPI().loadBannerPlace(
                            new BannerPlaceLoadSettings()
                                    .placeId(placeId)
                                    .uniqueId(uniqueId)
                                    .tags(
                                            tags.isEmpty() ? null :
                                                    new ArrayList<>(tags)
                                    )
                    );
                }
            });
        }
    }

    @Override
    public void loadBanners(boolean skipCache) {
        if (!skipCache) {
            if (core.widgetViewModels().bannerPlaceViewModels().copyFromCache(uniqueId, placeId))
                return;
        }
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.bannersAPI().loadBannerPlace(
                        new BannerPlaceLoadSettings()
                                .placeId(placeId)
                                .uniqueId(uniqueId)
                                .tags(
                                        tags.isEmpty() ? null :
                                                new ArrayList<>(tags)
                                )
                );
            }
        });
    }

    @Override
    public void dataIsCleared() {

    }

    @Override
    public boolean hasSubscribers(Observer<BannerListState> observer) {
        List<Observer<BannerListState>> subscribers = bannerPlaceStateObservable.getSubscribers();
        boolean hasUISubs = false;
        for (Observer<BannerListState> subscriber : subscribers) {
            if (subscriber == localObserver) continue;
            if (subscriber == observer) continue;
            hasUISubs = true;
        }
        return hasUISubs;
    }

    @NonNull
    @Override
    public String toString() {
        return "BannerListViewModel " + uid;
    }
}
