package com.inappstory.sdk.core.banners;

import java.util.Objects;

public class BannerViewModelKey {
    private final int bannerId;
    private final String bannerPlace;

    public BannerViewModelKey(int bannerId, String bannerPlace) {
        this.bannerId = bannerId;
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
        return Objects.hash(bannerId, bannerPlace);
    }
}
