package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

import java.util.UUID;

public class BannerPagerViewModel implements IBannerPagerViewModel {
    private final Observable<BannerPagerState> bannerPagerStateObservable =
            new Observable<>(new BannerPagerState());

    private final IASCore core;
    private final String bannerPlace;
    private final String uid = UUID.randomUUID().toString();

    public BannerPagerViewModel(IASCore core, String bannerPlace) {
        this.bannerPlace = bannerPlace;
        this.core = core;
        updateState(getCurrentBannerPagerState().copy().place(bannerPlace));
    }

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    @Override
    public BannerPagerState getCurrentBannerPagerState() {
        return bannerPagerStateObservable.getValue();
    }

    @Override
    public void updateState(BannerPagerState bannerPagerState) {
        bannerPagerStateObservable.updateValue(bannerPagerState);
    }

    @Override
    public void addSubscriber(Observer<BannerPagerState> observer) {
        this.bannerPagerStateObservable.subscribeAndGetValue(observer);
    }

    @Override
    public void removeSubscriber(Observer<BannerPagerState> observer) {
        this.bannerPagerStateObservable.unsubscribe(observer);
    }

    @NonNull
    @Override
    public String toString() {
        return "BannerPlaceViewModel " + uid;
    }
}
