package com.inappstory.sdk.banners;

import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public interface BannerListLoadCallback {
    void bannerPlaceLoaded(int size, String bannerPlace, List<BannerData> bannerData);
    void loadError(String bannerPlace);
    void firstBannerLoaded(int bannerId, String bannerPlace);
    void firstBannerLoadError(int bannerId, String bannerPlace);
}
