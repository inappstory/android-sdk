package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.banners.BannerPlaceLoadCallback;
import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;

public interface IBannerPlaceViewModel {
    BannerPlaceState getCurrentBannerPagerState();

    IBannerViewModel getBannerViewModel(int id);

    List<IBannerViewModel> getBannerViewModels();

    void updateState(BannerPlaceState bannerPlaceState);

    void addSubscriber(Observer<BannerPlaceState> observer);

    void removeSubscriber(Observer<BannerPlaceState> observer);

    void addBannerPlaceLoadCallback(BannerPlaceLoadCallback callback);

    void removeBannerPlaceLoadCallback(BannerPlaceLoadCallback callback);

    void updateCurrentIndex(int index);

    void showNext();

    void clear();

}
