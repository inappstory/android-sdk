package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TargetingBodyObject {
    @SerializedName("tags")
    List<String> tags;

    public TargetingBodyObject(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }
}
