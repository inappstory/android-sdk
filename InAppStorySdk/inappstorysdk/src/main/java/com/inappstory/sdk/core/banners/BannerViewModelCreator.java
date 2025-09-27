package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.IASCore;

public class BannerViewModelCreator implements IBannerViewModelCreator {

    private final int bannerId;
    private final String bannerPlace;
    private final String uid;
    private final IBannerPlaceViewModel bannerPlaceViewModel;
    private final IBannerContentViewModel bannerContentViewModel;

    public BannerViewModelCreator(
            int bannerId,
            String bannerPlace,
            String uid,
            IBannerPlaceViewModel bannerPlaceViewModel,
            IBannerContentViewModel bannerContentViewModel) {
        this.bannerId = bannerId;
        this.bannerPlace = bannerPlace;
        this.uid = uid;
        this.bannerPlaceViewModel = bannerPlaceViewModel;
        this.bannerContentViewModel = bannerContentViewModel;
    }

    public IBannerViewModel create(
            IASCore core
    ) {
        return new BannerViewModel(
                bannerId,
                bannerPlace,
                uid,
                core,
                bannerPlaceViewModel,
                bannerContentViewModel
        );
    }
}
