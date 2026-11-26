package com.inappstory.sdk.banners.ui;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.banners.BannerPlaceLoadCallback;
import com.inappstory.sdk.banners.IBannerHideCallback;

public interface IBannersWidget {
    void loadBanners();

    void reloadBanners();

    void uniqueId(String customUniqueId);

    void setPlaceId(final String placeId);

    void setAppearanceManager(AppearanceManager appearanceManager);

    void loadBanners(boolean skipCache);

    void loadCallback(BannerPlaceLoadCallback bannerPlaceLoadCallback);

    void bannerHideCallback(IBannerHideCallback removeCallback);
}
