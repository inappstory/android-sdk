package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

import java.util.Map;

public interface StoryWidgetCallback extends IASCallback {
    void widgetEvent(
            SlideData slideData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
