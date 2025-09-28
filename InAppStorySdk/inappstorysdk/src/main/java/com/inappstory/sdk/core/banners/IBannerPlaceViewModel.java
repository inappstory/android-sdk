package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;

public interface IBannerPlaceViewModel {
    BannerPlaceState getCurrentBannerPagerState();

    IBannerViewModel getBannerViewModel(int id, int index);

    void removeBannerViewModel(IBannerViewModel bannerViewModel);

    List<IBannerViewModel> getBannerViewModels();

    void updateState(BannerPlaceState bannerPlaceState);

    void addSubscriber(Observer<BannerPlaceState> observer);

    void addSubscriberAndCheckLocal(Observer<BannerPlaceState> observer);

    void removeSubscriber(Observer<BannerPlaceState> observer);

    void addBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback);

    void removeBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback);

    void updateCurrentIndex(int index);

    void showNext();

    void clear();

    void clearBanners();

    void reloadSubscriber();

    void dataIsCleared();

    boolean hasSubscribers(Observer<BannerPlaceState> observer);

}
