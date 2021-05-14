package com.inappstory.sdk.stories.ui.list;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.List;

import com.inappstory.sdk.stories.api.models.Image;

public class FavoriteImage {
    public int getId() {
        return id;
    }

    public List<Image> getImage() {
        return image;
    }

    private int id;

    public String getUrl() {
        if (!image.isEmpty()) return image.get(0).getUrl();
        return "";
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

    private List<Image> image;

    public int getBackgroundColor() {
        try {
            return Color.parseColor(backgroundColor);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public String backgroundColor;

    public FavoriteImage(int id, List<Image> image, String backgroundColor) {
        this.id = id;
        this.image = image;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FavoriteImage) {
            return ((FavoriteImage) other).id == id;
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
