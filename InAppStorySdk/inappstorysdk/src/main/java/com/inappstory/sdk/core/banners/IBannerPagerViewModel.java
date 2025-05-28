package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

public interface IBannerPagerViewModel {
    BannerPagerState getCurrentBannerPagerState();

    void addSubscriber(Observer<BannerPagerState> observer);
    void removeSubscriber(Observer<BannerPagerState> observer);
}
