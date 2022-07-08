package com.inappstory.sdk.stories.outercallbacks.common.reader;

import java.util.Map;

public interface StoryWidgetCallback {
    void widgetEvent(String widgetEventName,
                     Map<String, String> widgetData,
                     int storyId,
                     String storyTitle,
                     String feed,
                     int slidesCount,
                     int slideIndex,
                     String tags);
}
