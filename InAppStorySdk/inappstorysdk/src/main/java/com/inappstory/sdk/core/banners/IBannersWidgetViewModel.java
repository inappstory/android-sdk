package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.stories.utils.Observer;

import java.util.List;

public interface IBannersWidgetViewModel<T extends IBannerWidgetState> {
    T getCurrentBannerPlaceState();

    String placeId();

    void uniqueId(String uniqueId);

    void placeId(String placeId);

    void loadBanners(boolean skipCache);

    IBannerViewModel getBannerViewModel(int id, int index);

    void sendOpenStat(int bannerId, String iterationId);

    void removeBannerViewModel(IBannerViewModel bannerViewModel);

    List<IBannerViewModel> getBannerViewModels();

    void updateState(T bannerPlaceState);

    void addSubscriber(Observer<T> observer);

    void addSubscriberAndCheckLocal(Observer<T> observer);

    void removeSubscriber(Observer<T> observer);

    void addBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback);

    void removeBannerPlaceLoadCallback(InnerBannerPlaceLoadCallback callback);

    void updateCurrentIndex(int index);

    void showNext();

    void clear();

    void clearBanners();

    void reloadSubscriber();

    void dataIsCleared();

    boolean hasSubscribers(Observer<T> observer);

}
