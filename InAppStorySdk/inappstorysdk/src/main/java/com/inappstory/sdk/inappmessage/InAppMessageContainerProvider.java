package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;

public interface InAppMessageContainerProvider {
    InAppMessageContainerSettings provideContainer(InAppMessageData messageData);
    IAMViewController layoutController();
}
