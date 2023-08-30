package com.inappstory.sdk.stories.outercallbacks.common.reader;

import java.util.Map;

public interface StoryWidgetCallback {
    void widgetEvent(
            SlideData slideData,
            String widgetEventName,
            Map<String, String> widgetData
    );
}
