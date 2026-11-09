package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class StoryPlaceholder {
    @SerializedName(ModelFields.StoryPlaceholder.name)
    public String name;
    @SerializedName(ModelFields.StoryPlaceholder.defaultVal)
    public String defaultVal;
}
