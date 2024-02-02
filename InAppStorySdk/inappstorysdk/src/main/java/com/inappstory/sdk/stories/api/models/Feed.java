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

    @SerializedName("cover")
    public List<Image> feedCover;

    public String getFeedId() {
        if (feedId != null) return Integer.toString(feedId);
        return null;
    }

    public Image getProperCover(int quality) {
        if (feedCover == null || feedCover.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        switch (quality) {
            case Image.QUALITY_HIGH:
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
