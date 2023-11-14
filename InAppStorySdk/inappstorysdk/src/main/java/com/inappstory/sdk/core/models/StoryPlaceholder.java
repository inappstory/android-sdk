package com.inappstory.sdk.core.models;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

public class StoryPlaceholder {
    @SerializedName("name")
    public String name;
    @SerializedName("default_value")
    public String defaultVal;
}
