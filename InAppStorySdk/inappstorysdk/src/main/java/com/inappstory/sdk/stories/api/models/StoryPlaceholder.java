package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class StoryPlaceholder {
    @SerializedName("name")
    public String name;
    @SerializedName("default_value")
    public String defaultVal;
}
