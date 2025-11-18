package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;

public interface CloseInAppMessageCallback extends IASCallback {
    void closeInAppMessage(
            InAppMessageData storyData
    );
}
