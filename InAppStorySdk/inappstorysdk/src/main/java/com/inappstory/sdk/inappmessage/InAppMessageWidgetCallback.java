package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

import java.util.Map;

public interface InAppMessageWidgetCallback extends IASCallback {
    void inAppMessageWidget(
            InAppMessageData inAppMessageData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
