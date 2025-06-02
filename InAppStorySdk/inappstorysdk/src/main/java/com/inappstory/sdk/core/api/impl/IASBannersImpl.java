package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.banners.BannerLoadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASBanners;

public class IASBannersImpl implements IASBanners {
    private final IASCore core;

    public IASBannersImpl(IASCore core) {
        this.core = core;
    }


    @Override
    public void preload(BannerLoadCallback callback) {

    }

    @Override
    public void loadBannerPlace(String bannerPlace, BannerLoadCallback callback) {

    }

    @Override
    public void callback(BannerLoadCallback callback) {

    }
}
