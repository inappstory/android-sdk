package com.inappstory.sdk.stories.ui.list;

import android.graphics.Color;

import com.inappstory.sdk.core.dataholders.IFavoriteItem;

/**
 * Defines type for story cover in favorite cell. {@link #imageUrl()}
 * or {@link #backgroundColor()} to get cover.
 */
public class StoryFavoriteImage implements IFavoriteItem {
    public int id() {
        return id;
    }

    private int id;

    public String imageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    private final String imageUrl;

    public int backgroundColor() {
        try {
            return Color.parseColor(backgroundColor);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public final String backgroundColor;

    public StoryFavoriteImage(int id, String imageUrl, String backgroundColor) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
    }



    @Override
    public boolean equals(Object other) {
        if (other instanceof StoryFavoriteImage) {
            return ((StoryFavoriteImage) other).id == id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + id;
        result = 31 * result + id;
        return result;
    }
}
