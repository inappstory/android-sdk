package com.inappstory.sdk.refactoring.stories.data.network;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.network.annotations.models.SerializedName;

import java.util.List;

public class NFeed {
    @SerializedName("id")
    public Integer feedId;

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    @SerializedName("hasFavorite")
    public Boolean hasFavorite;
    @SerializedName("stories")
    public List<NStory> stories;
    @SerializedName("cover")
    public List<Image> feedCover;
}