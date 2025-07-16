package com.inappstory.sdk.banners;

public abstract class BannerPlaceLoadCallback implements IBannerPlaceLoadCallback {
    private final String bannerPlace;

    public BannerPlaceLoadCallback(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public String bannerPlace() {
        return bannerPlace;
    }
}
