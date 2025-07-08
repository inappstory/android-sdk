package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

public interface IBannerPlaceViewModel {
    BannerPlaceState getCurrentBannerPagerState();
    void updateState(BannerPlaceState bannerPlaceState);
    void addSubscriber(Observer<BannerPlaceState> observer);
    void removeSubscriber(Observer<BannerPlaceState> observer);

    void updateCurrentIndex(int index);

}
