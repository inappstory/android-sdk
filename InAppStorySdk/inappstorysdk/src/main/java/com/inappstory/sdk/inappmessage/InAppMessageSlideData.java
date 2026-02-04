package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;

public class InAppMessageSlideData extends ContentData {
    private int index;
    private InAppMessageData inAppMessage;
    private String payload;


    public InAppMessageSlideData(
            int index,
            String payload,
            InAppMessageData inAppMessage
    ) {
        super(inAppMessage.sourceType(), ContentType.IN_APP_MESSAGE);
        this.index = index;
        this.inAppMessage = inAppMessage;
        this.payload = payload;
    }

    public int index() {
        return index;
    }

    public InAppMessageData inAppMessage() {
        return inAppMessage;
    }

    public String payload() {
        return payload;
    }

    @Override
    public String toString() {
        return "IAMSlideData {" +
                "content=" + inAppMessage() +
                ", index='" + index() + '\'' +
                ", payload='" + payload() + '\'' +
                '}';
    }
}
