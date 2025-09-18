package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetingBodyObject {
    @SerializedName("tags")
    public List<String> tags;
    @SerializedName("options")
    public Map<String, String> options;

    public TargetingBodyObject() {}

    public TargetingBodyObject(List<String> tags, Map<String, String> options) {
        this.tags = new ArrayList<>(tags);
        this.options = new HashMap<>(options);
    }
}
