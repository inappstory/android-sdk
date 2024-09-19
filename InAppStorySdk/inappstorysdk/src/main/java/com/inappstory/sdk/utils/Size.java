package com.inappstory.sdk.utils;

import java.io.Serializable;

public class Size implements Serializable {
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private int width;
    private int height;
}
