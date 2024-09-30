package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface CloseStoryCallback extends IASCallback {

    void closeStory(
            SlideData slideData,
            CloseReader action
    );
}
