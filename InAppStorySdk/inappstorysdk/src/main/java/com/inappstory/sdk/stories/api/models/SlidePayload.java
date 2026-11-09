package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.data.ISlidePayload;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class SlidePayload implements ISlidePayload {
    @SerializedName(ModelFields.SlidePayload.eventType)
    public String eventType;

    @SerializedName(ModelFields.SlidePayload.slideIndex)
    public int slideIndex;

    @SerializedName(ModelFields.SlidePayload.payload)
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
