package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;

import java.util.List;

public interface IBannerPlaceLoadCallback extends IASCallback {
    void bannerPlaceLoaded(int size, List<BannerData> bannerData);
    void loadError();
    void bannerLoaded(int bannerId, boolean isCurrent);
    void bannerLoadError(int bannerId, boolean isCurrent);
}
