package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class StoryLoadedData {
    @SerializedName(ModelFields.StoryLoadedData.id)
    public int id;
    @SerializedName(ModelFields.StoryLoadedData.index)
    public int index;
}
