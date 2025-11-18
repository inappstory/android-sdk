package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

public interface ShowInAppMessageSlideCallback extends IASCallback {
    void showSlide(
            InAppMessageData inAppMessageData, int slideIndex
    );
}
