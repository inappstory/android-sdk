package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BannerPlaceFilterObject {
    @SerializedName("tags")
    List<String> tags;

    public BannerPlaceFilterObject(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }
}
