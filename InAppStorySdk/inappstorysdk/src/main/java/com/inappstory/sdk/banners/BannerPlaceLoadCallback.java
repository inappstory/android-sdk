package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.core.data.IBannerPlace;

public interface BannerPlaceLoadCallback extends IASCallback {
    void loaded(IBannerPlace bannerPlace);

    void loadError(String bannerPlace, String reason);

    void isEmpty(String bannerPlace);
}
