package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

public interface ShowInAppMessageCallback extends IASCallback {
    void showInAppMessage(
            InAppMessageData storyData
    );
}
