package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

public interface IBannerViewModel {
    BannerState getCurrentBannerState();

    void addSubscriber(Observer<BannerState> observer);
    void removeSubscriber(Observer<BannerState> observer);
}
