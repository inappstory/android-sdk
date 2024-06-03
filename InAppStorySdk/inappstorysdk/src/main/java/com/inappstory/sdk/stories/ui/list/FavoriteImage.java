package com.inappstory.sdk.stories.ui.list;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.inappstory.sdk.stories.api.models.Image;

import java.util.List;

/**
 * Defines type for story cover in favorite cell. Use {@link #getImage()}, {@link #getUrl()}
 * or {@link #getBackgroundColor()} to get cover.
 */
public class FavoriteImage {
    public int getId() {
        return id;
    }

    /**
     * @return {@link Image} instance with cover of Story with lowest quality
     */
    public Image getImage() {
        if (image == null || image.isEmpty())
            return null;
        return image.get(0);
    }

    /**
     * @return {@link Image} instance with cover of Story with custom quality
     */
    public Image getImage(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        switch (quality) {
            case Image.QUALITY_HIGH:
                q = Image.TYPE_HIGH;
        }
        for (Image img : image) {
            if (img.getType().equals(q)) return img;
        }
        return image.get(0);
    }


    private int id;

    public String getUrl() {
        if (image != null && !image.isEmpty()) return image.get(0).getUrl();
        return "";
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

    private List<Image> image;

    @Override
    public String toString() {
        return "FavoriteImage{" +
                "id=" + id +
                ", bitmap=" + bitmap +
                ", image=" + image +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }

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
