package com.inappstory.sdk.core.banners;

import java.util.Objects;

public class ShownBannerKey {
    String iterationId;
    int bannerId;

    public ShownBannerKey(String iterationId, int bannerId) {
        this.iterationId = iterationId;
        this.bannerId = bannerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShownBannerKey that = (ShownBannerKey) o;
        return bannerId == that.bannerId && Objects.equals(iterationId, that.iterationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iterationId, bannerId);
    }
}
