package com.inappstory.sdk.core.banners;

import java.util.Objects;

public class BannerPlaceViewModelKey {
    final String uniqueId;
    final String bannerPlace;

    public BannerPlaceViewModelKey(String uniqueId, String bannerPlace) {
        this.uniqueId = uniqueId;
        this.bannerPlace = bannerPlace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BannerPlaceViewModelKey that = (BannerPlaceViewModelKey) o;
        return Objects.equals(uniqueId, that.uniqueId) &&
                Objects.equals(bannerPlace, that.bannerPlace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, bannerPlace);
    }
}
