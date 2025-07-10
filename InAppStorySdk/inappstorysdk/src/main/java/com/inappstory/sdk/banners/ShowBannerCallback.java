package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

public interface ShowBannerCallback extends IASCallback {
    void showBanner(
            BannerData storyData
    );
}
