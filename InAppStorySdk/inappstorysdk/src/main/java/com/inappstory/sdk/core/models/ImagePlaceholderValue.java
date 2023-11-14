package com.inappstory.sdk.core.models;

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

    public static ImagePlaceholderValue createByUrl(String url) {
        return new ImagePlaceholderValue(ImagePlaceholderType.URL, url);
    }
}
