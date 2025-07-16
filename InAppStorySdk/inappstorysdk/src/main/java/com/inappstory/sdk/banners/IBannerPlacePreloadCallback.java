package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public interface IBannerPlacePreloadCallback extends IASCallback {
    void bannerPlaceLoaded(int size, List<BannerData> bannerData);
    void loadError();
    void bannerContentLoaded(int bannerId, boolean isFirst);
    void bannerContentLoadError(int bannerId, boolean isFirst);
}
