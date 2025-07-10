package com.inappstory.sdk.banners;

import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public interface BannerPlaceCallback {
    void bannersLoaded(
            int size,
            String placeId,
            List<BannerData> bannersData
    );

    void loadError(String placeId);

    void onBannerShown();
}
