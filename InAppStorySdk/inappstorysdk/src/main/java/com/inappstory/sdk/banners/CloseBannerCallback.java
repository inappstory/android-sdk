package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

public interface CloseBannerCallback extends IASCallback {
    void closeBanner(
            BannerData storyData
    );
}
