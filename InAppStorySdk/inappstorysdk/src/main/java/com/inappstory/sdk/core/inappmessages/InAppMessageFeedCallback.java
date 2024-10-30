package com.inappstory.sdk.core.inappmessages;

import com.inappstory.sdk.core.dataholders.models.IReaderContent;

import java.util.List;

public interface InAppMessageFeedCallback {
    void success(List<IReaderContent> content);
    void isEmpty();
    void error();
}
