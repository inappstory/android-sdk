package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.BannerData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

import java.util.Map;

public interface BannerWidgetCallback extends IASCallback {
    void bannerWidget(
            BannerData inAppMessageData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
