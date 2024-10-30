package com.inappstory.sdk.core.inappmessages;

import com.inappstory.sdk.core.dataholders.models.IReaderContent;

import java.util.List;

public interface InAppMessageByIdCallback {
    void success(IReaderContent content);
    void error();
}
