package com.inappstory.sdk.core.banners;

import java.util.Objects;

public class BannerViewModelKey {
    private final int bannerId;
    private final int bannerIndex;
    private final String bannerPlace;

    public boolean correct(int bannerId, String bannerPlace) {
        return this.bannerPlace.equals(bannerPlace) && this.bannerId == bannerId;
    }

    public BannerViewModelKey(int bannerId, int bannerIndex, String bannerPlace) {
        this.bannerId = bannerId;
        this.bannerIndex = bannerIndex;
        this.bannerPlace = bannerPlace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BannerViewModelKey that = (BannerViewModelKey) o;
        return bannerId == that.bannerId && Objects.equals(bannerPlace, that.bannerPlace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bannerId, bannerPlace, bannerIndex);
    }
}
