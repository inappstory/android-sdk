package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface ShowSlideCallback extends IASCallback {

    void showSlide(
            SlideData slideData
    );
}
