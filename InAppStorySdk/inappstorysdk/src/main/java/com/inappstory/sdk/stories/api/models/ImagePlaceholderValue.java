package com.inappstory.sdk.stories.api.models;

import androidx.annotation.Nullable;

public class ImagePlaceholderValue {
    private ImagePlaceholderType type;
    private String url;

    public String getUrl() {
        return url;
    }

    public ImagePlaceholderType getType() {
        return type;
    }

    private ImagePlaceholderValue(ImagePlaceholderType type, String url) {
        this.type = type;
        this.url = url;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ImagePlaceholderValue) {
            return
                    url.equals(((ImagePlaceholderValue) obj).url) && type.equals(((ImagePlaceholderValue) obj).type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode() * 37 + type.hashCode();
    }

    public static ImagePlaceholderValue createByUrl(String url) {
        return new ImagePlaceholderValue(ImagePlaceholderType.URL, url);
    }
}
