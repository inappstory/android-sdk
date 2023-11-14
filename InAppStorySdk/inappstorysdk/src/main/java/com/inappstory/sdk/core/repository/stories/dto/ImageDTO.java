package com.inappstory.sdk.core.repository.stories.dto;

import com.inappstory.sdk.core.models.api.Image;

public class ImageDTO implements IImageDTO {
    private final String url;
    private final int width;
    private final int height;
    private final int expire;
    private final String type;

    public ImageDTO(Image image) {
        this.url = image.getUrl();
        this.type = image.getType();
        this.expire = image.getExpire();
        this.height = image.getHeight();
        this.width = image.getWidth();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getWidth() {
        if (width <= 0) return 1;
        return width;
    }

    @Override
    public int getHeight() {
        if (height <= 0) return 1;
        return height;
    }

    @Override
    public int getExpire() {
        return expire;
    }

    @Override
    public String getType() {
        return type;
    }
}
