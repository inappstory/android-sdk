package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

public class BannerPlaceViewModelCreator implements IBannerPlaceViewModelCreator {

    private final String bannerPlace;

    public BannerPlaceViewModelCreator(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public IBannerPlaceViewModel create(
            IASCore core
    ) {
        return new BannerPlaceViewModel(
                core,
                bannerPlace
        );
    }
}
