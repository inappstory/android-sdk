package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BannerPlaceViewModel implements IBannerPlaceViewModel {
    private final Observable<BannerPlaceState> bannerPlaceStateObservable =
            new Observable<>(new BannerPlaceState());

    private final IASCore core;
    private final String bannerPlace;
    private final String uid = UUID.randomUUID().toString();
    BannerViewModelsHolder bannerViewModelsHolder;

    public BannerPlaceViewModel(IASCore core, String bannerPlace) {
        this.bannerPlace = bannerPlace;
        bannerViewModelsHolder = new BannerViewModelsHolder(core, this);
        this.core = core;
        updateState(getCurrentBannerPagerState().copy().place(bannerPlace));
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

    @Override
    public void updateState(BannerPlaceState bannerPlaceState) {
        bannerPlaceStateObservable.updateValue(bannerPlaceState);
    }

    @Override
    public void addSubscriber(Observer<BannerPlaceState> observer) {
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
        List<IBanner> items = placeState.items;
        int total = items.size();
        int realIndex = index % total;
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
        bannerPlaceStateObservable.setValue(bannerPlaceStateObservable.getValue().copy().currentIndex(realIndex));
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

    @NonNull
    @Override
    public String toString() {
        return "BannerPlaceViewModel " + uid;
    }
}
