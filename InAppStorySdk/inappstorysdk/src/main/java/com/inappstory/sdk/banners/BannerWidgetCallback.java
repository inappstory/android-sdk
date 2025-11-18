package com.inappstory.sdk.banners;

import com.inappstory.sdk.core.api.IASCallback;

import java.util.Map;

public interface BannerWidgetCallback extends IASCallback {
    void bannerWidget(
            BannerData bannerData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
