package com.inappstory.sdk.core.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.core.data.IBanner;

import java.util.List;

public interface IBannerPlaceLoadCallback extends IASCallback {
    void bannerPlaceLoaded(List<IBanner> banners);
    void loadError();
    void bannerLoaded(int bannerId, boolean isCurrent);
    void bannerLoadError(int bannerId, boolean isCurrent);
}
