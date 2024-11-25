package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBSLineAppearance;

public class InAppMessageBSLineSettings implements InAppMessageBSLineAppearance {
    public InAppMessageBSLineSettings(
            Integer topMargin,
            Integer width,
            Integer height,
            String color
    ) {
        this.topMargin = topMargin;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public InAppMessageBSLineSettings() {}

    public Integer topMargin;
    public Integer width;
    public Integer height;
    public String color;

    @Override
    public int topMargin() {
        return topMargin != null ? topMargin : 8;
    }

    @Override
    public int width() {
        return width != null ? width : 32;
    }

    @Override
    public int height() {
        return height != null ? height : 4;
    }

    @Override
    public String color() {
        return color != null ? color : "#00000080";
    }
}
