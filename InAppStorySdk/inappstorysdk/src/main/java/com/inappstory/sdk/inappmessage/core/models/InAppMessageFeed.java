package com.inappstory.sdk.inappmessage.core.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageFeed implements IInAppMessageFeed<InAppMessage> {
    public List<InAppMessage> messages = new ArrayList<>();

    public InAppMessageFeed(IInAppMessageFeed feed) {
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
        return messages;
    }
}
