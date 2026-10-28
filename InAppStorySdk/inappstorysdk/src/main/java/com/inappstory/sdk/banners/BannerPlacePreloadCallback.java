package com.inappstory.sdk.banners;

public abstract class BannerPlacePreloadCallback implements IBannerPlacePreloadCallback {
    private final String bannerPlace;

    public BannerPlacePreloadCallback(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public String bannerPlace() {
        return bannerPlace;
    }
}
