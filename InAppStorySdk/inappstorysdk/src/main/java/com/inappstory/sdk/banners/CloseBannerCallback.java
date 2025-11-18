package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;

public interface CloseBannerCallback extends IASCallback {
    void closeBanner(
            BannerData storyData
    );
}
