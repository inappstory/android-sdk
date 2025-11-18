package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;

public interface ShowBannerCallback extends IASCallback {
    void showBanner(
            BannerData bannerData
    );
}
