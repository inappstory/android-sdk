package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.data.ISlidePayload;
import com.inappstory.sdk.network.annotations.models.SerializedName;

public class SlidePayload implements ISlidePayload {
    @SerializedName("event_type")
    public String eventType;

    @SerializedName("slide_index")
    public int slideIndex;

    public String payload;

    public String eventType() {
        if (eventType != null) return eventType;
        return "";
    }

    public String payload() {
        return payload;
    }

    public int slideIndex() {
        return slideIndex;
    }
}
