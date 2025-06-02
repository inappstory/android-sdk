package com.inappstory.sdk.core.api;

import com.inappstory.sdk.banners.BannerLoadCallback;

public interface IASBanners {
    void preload(
            BannerLoadCallback callback
    );

    void loadBannerPlace(
            String bannerPlace,
            BannerLoadCallback callback
    );

    void callback(
            BannerLoadCallback callback
    );
}
