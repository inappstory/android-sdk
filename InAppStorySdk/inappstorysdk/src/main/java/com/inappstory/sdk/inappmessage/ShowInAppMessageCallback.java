package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.InAppMessageData;

public interface ShowInAppMessageCallback extends IASCallback {
    void showInAppMessage(
            InAppMessageData storyData
    );
}
