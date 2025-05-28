package com.inappstory.sdk.core.inappmessages;

import com.inappstory.sdk.core.data.IInAppMessageLimit;

import java.util.List;

public interface InAppMessagesLimitCallback {
    void success(List<IInAppMessageLimit> limits);
    void error();
}
