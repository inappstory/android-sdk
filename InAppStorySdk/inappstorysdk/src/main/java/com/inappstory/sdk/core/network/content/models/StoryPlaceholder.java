package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class StoryPlaceholder {
    @SerializedName("name")
    public String name;
    @SerializedName("default_value")
    public String defaultVal;
}
