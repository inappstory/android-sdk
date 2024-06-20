package com.inappstory.sdk.packages.features.stories.models.network;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryResource {
    @SerializedName("url")
    public String url;

    @SerializedName("key")
    public String key;

    @SerializedName("type")
    public String type;

    @SerializedName("purpose")
    public String purpose;

    @SerializedName("filename")
    public String filename;

    @SerializedName("slide_index")
    public Integer slideIndex;

    @SerializedName("range_start")
    public Long rangeStart;

    @SerializedName("range_end")
    public Long rangeEnd;
}
