package com.inappstory.sdk.packages.features.stories.models.network;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryPayload {
    @SerializedName("event_type")
    public String eventType;

    @SerializedName("slide_index")
    public int slideIndex;
}
