package com.inappstory.sdk.core.models.api;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class PayloadObject {
    @SerializedName("event_type")
    public String eventType;

    public int getSlideIndex() {
        return slideIndex;
    }

    @SerializedName("slide_index")
    public int slideIndex;

    public String getEventType() {
        if (eventType != null) return eventType;
        return "";
    }

    public String payload;

    public String getPayload() {
        return payload;
    }
}