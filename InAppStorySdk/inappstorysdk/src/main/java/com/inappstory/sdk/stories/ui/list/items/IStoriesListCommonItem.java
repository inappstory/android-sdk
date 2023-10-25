package com.inappstory.sdk.stories.ui.list.items;

import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.ui.list.ClickCallback;

public interface IStoriesListCommonItem {
    void bindCommon(
            Integer id,
            StoryData storyData,
            String titleText,
            Integer titleColor,
            Integer backgroundColor,
            boolean isOpened,
            boolean hasAudio,
            ClickCallback callback
    );
}
