package com.inappstory.sdk.core.inappmessages;

import com.inappstory.sdk.core.data.IReaderContent;

public interface InAppMessageByIdCallback {
    void success(IReaderContent content);
    void error();
}
