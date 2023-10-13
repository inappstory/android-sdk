package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public interface CloseStoryCallback {

    void closeStory(
            SlideData slideData,
            CloseReader action
    );
}
