package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface ClickOnShareStoryCallback extends IASCallback {
    void shareClick(
            SlideData slideData
    );
}
