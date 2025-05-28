package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observable;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public class BannerViewModel implements IBannerViewModel {

    private final int bannerId;
    private final Observable<BannerState> stateObservable =
            new Observable<>(new BannerState());

    public SingleTimeEvent<STETypeAndData> singleTimeEvents() {
        return singleTimeEvents;
    }

    private final SingleTimeEvent<STETypeAndData> singleTimeEvents =
            new SingleTimeEvent<>();

    public BannerViewModel(int bannerId) {
        this.bannerId = bannerId;
    }

    @Override
    public BannerState getCurrentBannerState() {
        return stateObservable.getValue();
    }

    @Override
    public void addSubscriber(Observer<BannerState> observable) {
        this.stateObservable.subscribe(observable);
    }

    @Override
    public void removeSubscriber(Observer<BannerState> observable) {
        this.stateObservable.unsubscribe(observable);
    }
}
