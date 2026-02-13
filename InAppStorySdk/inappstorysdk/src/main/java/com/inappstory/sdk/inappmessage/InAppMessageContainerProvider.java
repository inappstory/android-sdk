package com.inappstory.sdk.inappmessage;

public interface InAppMessageContainerProvider {
    InAppMessageContainerSettings provideContainer(InAppMessageData messageData);
}
