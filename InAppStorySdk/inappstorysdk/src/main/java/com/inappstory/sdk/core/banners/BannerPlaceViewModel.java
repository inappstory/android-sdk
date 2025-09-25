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
import java.util.Set;
import java.util.UUID;

public class BannerPlaceViewModel implements IBannerPlaceViewModel {
    private final Observable<BannerPlaceState> bannerPlaceStateObservable =
            new Observable<>(new BannerPlaceState());

    private final IASCore core;
    private final String bannerPlace;
    private final String uid = UUID.randomUUID().toString();
    private final List<String> tags = new ArrayList<>();
    BannerViewModelsHolder bannerViewModelsHolder;

    public BannerPlaceViewModel(IASCore core, String bannerPlace) {
        this.bannerPlace = bannerPlace;
        bannerViewModelsHolder = new BannerViewModelsHolder(core, this);
        this.core = core;
        addSubscriber(localObserver);
        updateState(new BannerPlaceState().place(bannerPlace));
    }

    private final Set<InnerBannerPlaceLoadCallback> callbacks = new HashSet<>();

    private final Object callbacksLock = new Object();

    @Override
    public void addBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback) {
        synchronized (callbacksLock) {
            callbacks.add(callback);
        }
        BannerPlaceState placeState = getCurrentBannerPagerState();
        List<IBanner> content = placeState.getItems();
        if (placeState.tags() != null) {
            tags.clear();
            tags.addAll(placeState.tags());
        }
        switch (placeState.loadState()) {
            case EMPTY:
                /*try {
                    callback.bannerPlaceLoaded(
                            content
                    );
                } catch (Exception e) {
                }*/
                break;
            case FAILED:
               /* try {
                    callback.loadError();
                } catch (Exception e) {
                }*/
                break;
            case NONE:
            case LOADING:
                break;
            case LOADED:
                /*if (content != null) {
                    try {
                        callback.bannerPlaceLoaded(
                                content
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
                break;
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
    public BannerPlaceState getCurrentBannerPagerState() {
        return bannerPlaceStateObservable.getValue();
    }

    Observer<BannerPlaceState> localObserver = new Observer<BannerPlaceState>() {
        BannerPlaceLoadStates bannerPlaceLoadState = BannerPlaceLoadStates.NONE;

        @Override
        public void onUpdate(BannerPlaceState newValue) {
            if (newValue.loadState() == null || bannerPlaceLoadState.equals(newValue.loadState()))
                return;
            Set<InnerBannerPlaceLoadCallback> callbacks = new HashSet<>();
            List<IBanner> content = newValue.getItems();
            synchronized (callbacksLock) {
                callbacks.addAll(BannerPlaceViewModel.this.callbacks);
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
                    break;
                case LOADING:
                    break;
                case LOADED:
                    break;
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
    public void updateState(BannerPlaceState bannerPlaceState) {
        bannerPlaceStateObservable.updateValue(bannerPlaceState);
    }

    @Override
    public void addSubscriber(Observer<BannerPlaceState> observer) {
        this.bannerPlaceStateObservable.subscribeAndGetValue(observer);
    }

    @Override
    public void addSubscriberAndCheckLocal(Observer<BannerPlaceState> observer) {
        this.bannerPlaceStateObservable.subscribeAndGetValue(observer);
    }

    @Override
    public void removeSubscriber(Observer<BannerPlaceState> observer) {
        this.bannerPlaceStateObservable.unsubscribe(observer);
    }

    @Override
    public IBannerViewModel getBannerViewModel(int id) {
        return bannerViewModelsHolder
                .get(
                        id,
                        bannerPlace
                );
    }

    @Override
    public List<IBannerViewModel> getBannerViewModels() {
        BannerPlaceState placeState = bannerPlaceStateObservable.getValue();
        List<IBanner> items = placeState.items;
        List<IBannerViewModel> bannerViewModels = new ArrayList<>();
        if (items == null) return bannerViewModels;
        for (IBanner banner : items) {
            bannerViewModels.add(bannerViewModelsHolder.get(banner.id(), bannerPlace));
        }
        return bannerViewModels;
    }

    @Override
    public void updateCurrentIndex(int index) {
        BannerPlaceState placeState = bannerPlaceStateObservable.getValue();
        bannerPlaceStateObservable.setValue(placeState.copy().currentIndex(index));
        List<IBanner> items = placeState.items;
        int total = items.size();
        if (total == 0) return;
        int realIndex = index % total;
        int prevInd = (realIndex - 1 + total) % total;
        int nextInd = (realIndex + 1) % total;
        BannerDownloadManager bannerDownloadManager = core.contentLoader().bannerDownloadManager();
        bannerDownloadManager.setMaxPriority(items.get(prevInd).id(), true);
        bannerDownloadManager.setMaxPriority(items.get(nextInd).id(), true);
        bannerDownloadManager.setMaxPriority(items.get(realIndex).id(), true);
        for (int i = 0; i < items.size(); i++) {
            IBannerViewModel bannerViewModel = bannerViewModelsHolder
                    .get(
                            items.get(i).id(),
                            bannerPlace
                    );
            if (bannerViewModel != null) {
                if (i == realIndex) {
                    bannerViewModel.bannerIsActive(true);
                } else {
                    bannerViewModel.bannerIsActive(false);
                    bannerViewModel.stopSlide();
                }
            }

        }
       /* bannerPlaceStateObservable.updateValue(

        );*/
    }

    @Override
    public void showNext() {
        BannerPlaceState placeState = getCurrentBannerPagerState();
        int newIndex = 1;
        Integer currentIndex = placeState.currentIndex;
        if (currentIndex != null) newIndex = currentIndex + 1;
        bannerPlaceStateObservable.updateValue(placeState.copy().currentIndex(newIndex));
    }

    @Override
    public void clear() {
        BannerPlaceState bannerPlaceState = bannerPlaceStateObservable.getValue();
        List<String> tags = null;
        if (bannerPlaceState != null) tags = bannerPlaceState.tags();
        bannerPlaceStateObservable.setValue(
                new BannerPlaceState()
                        .place(bannerPlace)
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
        List<Observer<BannerPlaceState>> subscribers = bannerPlaceStateObservable.getSubscribers();
        boolean hasUISubs = false;
        for (Observer<BannerPlaceState> subscriber : subscribers) {
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
                                    .placeId(bannerPlace)
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
    public void dataIsCleared() {

    }

    @Override
    public boolean hasSubscribers(Observer<BannerPlaceState> observer) {
        List<Observer<BannerPlaceState>> subscribers = bannerPlaceStateObservable.getSubscribers();
        boolean hasUISubs = false;
        for (Observer<BannerPlaceState> subscriber : subscribers) {
            if (subscriber == localObserver) continue;
            if (subscriber == observer) continue;
            hasUISubs = true;
        }
        return hasUISubs;
    }

    @NonNull
    @Override
    public String toString() {
        return "BannerPlaceViewModel " + uid;
    }
}
