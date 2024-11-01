package com.inappstory.sdk.core.network.content.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.core.data.IInAppMessageFeed;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageFeed implements IInAppMessageFeed<InAppMessage> {

    @SerializedName("messages")
    public List<InAppMessage> messages;

    public InAppMessageFeed(IInAppMessageFeed<IInAppMessage> feed) {
        if (messages == null) messages = new ArrayList<>();
        if (feed.messages() != null) {
            for (Object message : feed.messages()) {
                messages.add((InAppMessage) message);
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
