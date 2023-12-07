package com.inappstory.sdk.core.models.api;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.utils.network.annotations.models.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Feed {
    @SerializedName("hasFavorite")
    public Boolean hasFavorite;
    @SerializedName("stories")
    public List<Story> stories;

    @NonNull
    public List<Story> getStories() {
        if (stories == null) return new ArrayList<>();
        return stories;
    }

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }
}