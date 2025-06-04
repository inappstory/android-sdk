package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.core.data.IBanner;

import java.util.List;

public interface BannerLoadCallback extends IASCallback {
    void loaded(int id);

    void allLoaded();

    void bannerPlaceLoaded(List<IBanner> banners);

    void loadError(int id);

    void loadError();

    void isEmpty();
}
