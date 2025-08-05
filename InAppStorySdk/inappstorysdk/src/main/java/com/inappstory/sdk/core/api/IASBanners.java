package com.inappstory.sdk.core.api;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.banners.BannerPlaceLoadSettings;

public interface IASBanners {

    void preload(
            BannerPlaceLoadSettings bannerPlace,
            BannerPlacePreloadCallback callback
    );

    void loadBannerPlace(
            BannerPlaceLoadSettings bannerPlace
    );

}
