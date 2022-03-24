package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.SerializedName;

import java.util.List;

public class Feed {
    public Boolean hasFavorite;
    @SerializedName("stories")
    public List<Story> stories;

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }
}
