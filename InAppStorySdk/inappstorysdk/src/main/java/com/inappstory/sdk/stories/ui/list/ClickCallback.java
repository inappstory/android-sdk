package com.inappstory.sdk.stories.ui.list;

import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;

public interface ClickCallback {
    void onItemClick(int index, StoryItemCoordinates coordinates);
}
