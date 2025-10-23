package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

public interface ShowInAppMessageSlideCallback extends IASCallback {
    void showSlide(
            InAppMessageData inAppMessageData, int slideIndex
    );
}
