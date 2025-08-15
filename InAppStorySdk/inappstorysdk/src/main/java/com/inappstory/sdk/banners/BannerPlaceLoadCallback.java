package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public abstract class BannerPlaceLoadCallback implements IBannerPlaceLoadCallback {
    public void bannerPlace(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    private String bannerPlace;


    public final void bannerPlaceLoaded(List<IBanner> banners) {

    }

    public abstract void bannerPlaceLoaded(int size, List<BannerData> bannerData, int widgetHeight);



    public BannerPlaceLoadCallback(String bannerPlace) {
        this.bannerPlace = bannerPlace;
    }

    public BannerPlaceLoadCallback() {
    }

    public final String bannerPlace() {
        return bannerPlace;
    }
}
