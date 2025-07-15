package com.inappstory.sdk.core.api;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannersLoadCallback;

public interface IASBanners {

    void preload(
            String bannerPlace,
            BannersLoadCallback callback
    );

    void loadBannerPlace(
            String bannerPlace
    );

    void addPreloadCallback(
            @NonNull BannersLoadCallback callback
    );

    void removePreloadCallback(
            @NonNull BannersLoadCallback callback
    );

    BannersLoadCallback getPreloadCallback(String bannerPlace);
}
