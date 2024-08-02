package com.inappstory.sdk.inappmessage.core.models;

import androidx.annotation.NonNull;

import java.util.List;

public class NetworkInAppMessageFeed implements IInAppMessageFeed<NetworkInAppMessage> {
    public List<NetworkInAppMessage> messages;

    @NonNull
    @Override
    public List<NetworkInAppMessage> messages() {
        return messages;
    }
}
