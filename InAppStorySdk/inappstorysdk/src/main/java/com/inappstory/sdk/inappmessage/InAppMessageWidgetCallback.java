package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

import java.util.Map;

public interface InAppMessageWidgetCallback extends IASCallback {
    void inAppMessageWidget(
            InAppMessageData inAppMessageData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
