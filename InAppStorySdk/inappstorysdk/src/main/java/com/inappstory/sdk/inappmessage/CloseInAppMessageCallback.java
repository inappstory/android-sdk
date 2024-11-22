package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

public interface CloseInAppMessageCallback extends IASCallback {
    void closeInAppMessage(
            InAppMessageData storyData
    );
}
