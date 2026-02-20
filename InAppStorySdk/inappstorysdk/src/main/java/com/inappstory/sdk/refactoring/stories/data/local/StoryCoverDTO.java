package com.inappstory.sdk.refactoring.stories.data.local;

import android.graphics.Color;

import com.inappstory.sdk.core.data.IFavoriteItem;
import com.inappstory.sdk.core.utils.ColorUtils;

import java.util.Objects;

public class StoryCoverDTO implements IFavoriteItem {

    private final int id;

    private final String imageUrl;

    private final String backgroundColor;

    public StoryCoverDTO(
            int id,
            String imageUrl,
            String backgroundColor
    ) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String imageUrl() {
        return imageUrl;
    }

    @Override
    public int backgroundColor() {
        try {
            return ColorUtils.parseColorRGBA(backgroundColor);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryCoverDTO)) return false;
        StoryCoverDTO that = (StoryCoverDTO) o;
        return id == that.id &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(backgroundColor, that.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, backgroundColor);
    }
}
