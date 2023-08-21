package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class Feed {
    @SerializedName("id")
    public Integer feedId;
    @SerializedName("hasFavorite")
    public Boolean hasFavorite;
    @SerializedName("stories")
    public List<Story> stories;

    public String getFeedId() {
        if (feedId != null) return Integer.toString(feedId);
        return null;
    }

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }
}
