package com.inappstory.sdk.inappmessage.core.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageFeed implements IInAppMessageFeed<InAppMessage> {

    @SerializedName("messages")
    public List<InAppMessage> messages;

    public InAppMessageFeed(IInAppMessageFeed feed) {
        if (messages == null) messages = new ArrayList<>();
        if (feed.messages() != null) {
            for (Object message : feed.messages()) {
                if (message instanceof IInAppMessage) {
                    messages.add((InAppMessage) message);
                }
            }
        }
    }

    @NonNull
    @Override
    public List<InAppMessage> messages() {
        if (messages == null) messages = new ArrayList<>();
        return messages;
    }
}
