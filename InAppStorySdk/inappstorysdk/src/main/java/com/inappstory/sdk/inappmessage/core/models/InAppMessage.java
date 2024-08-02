package com.inappstory.sdk.inappmessage.core.models;

public class InAppMessage implements IInAppMessage {
    private final int id;

    public InAppMessage(IInAppMessage message) {
        this.id = message.id();
    }

    @Override
    public int id() {
        return id;
    }
}
