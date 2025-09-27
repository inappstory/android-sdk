package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;

public interface IBannerPlaceViewModel {
    BannerPlaceState getCurrentBannerPagerState();

    List<Integer> getBannersIds();

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
