package com.inappstory.sdk.core.api;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;

public interface IASBanners {

    void preload(
            String bannerPlace,
            BannerPlacePreloadCallback callback
    );

    void loadBannerPlace(
            String bannerPlace
    );

}
