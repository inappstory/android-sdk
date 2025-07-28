package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public abstract class BannerPlaceLoadCallback implements IBannerPlaceLoadCallback {
    private final String bannerPlace;


    public final void bannerPlaceLoaded(List<IBanner> banners) {

    }


    abstract void bannerPlaceLoaded(int size, List<BannerData> bannerData, int widgetHeight);

    public BannerPlaceLoadCallback(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public String bannerPlace() {
        return bannerPlace;
    }
}
