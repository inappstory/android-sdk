package com.inappstory.sdk.banners;

public abstract class BannersLoadCallback implements IBannersLoadCallback {
    private final String bannerPlace;

    public BannersLoadCallback(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public String bannerPlace() {
        return bannerPlace;
    }
}
