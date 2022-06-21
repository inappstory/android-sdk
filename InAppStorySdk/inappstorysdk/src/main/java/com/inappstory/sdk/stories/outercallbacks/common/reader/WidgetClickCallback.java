package com.inappstory.sdk.stories.outercallbacks.common.reader;

import java.util.Map;

public interface WidgetClickCallback {
    void widgetClick(String widgetName,
                     Map<String, String> widgetData,
                     int storyId,
                     String storyTitle,
                     String feed,
                     int slidesCount,
                     int slideIndex,
                     String tags);
}
