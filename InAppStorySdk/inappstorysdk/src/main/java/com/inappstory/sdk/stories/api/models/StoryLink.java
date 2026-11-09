package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class StoryLink {
    public StoryLink(String type, String target) {
        this.type = type;
        this.target = target;
    }

    public StoryLink() {

    }

    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    @SerializedName(ModelFields.StoryLink.type)
    public String type;
    @SerializedName(ModelFields.StoryLink.target)
    public String target;
}