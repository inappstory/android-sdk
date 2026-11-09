package com.inappstory.sdk.core.network.content.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.List;

public class Feed {
    @SerializedName(ModelFields.Feed.feedId)
    public Integer feedId;
    @SerializedName(ModelFields.Feed.hasFavorite)
    public Boolean hasFavorite;
    @SerializedName(ModelFields.Feed.stories)
    public List<Story> stories;
    @SerializedName(ModelFields.Feed.feedCover)
    public List<Image> feedCover;

    public String getFeedId() {
        if (feedId != null) return Integer.toString(feedId);
        return null;
    }

    public Image getProperCover(int quality) {
        if (feedCover == null || feedCover.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        if (quality == Image.QUALITY_HIGH) {
            q = Image.TYPE_HIGH;
        }
        for (Image img : feedCover) {
            if (img.getType().equals(q)) return img;
        }
        return feedCover.get(0);
    }

    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }
}
