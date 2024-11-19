package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;

public class InAppMessageModalSettings implements InAppMessageModalAppearance {
    public Integer maxHeight;
    public Integer cornerRadius;
    public Integer horizontalOffset;
    public Integer maxWidth;
    public String backgroundColor;

    @Override
    public int maxHeight() {
        return maxHeight != null ? maxHeight : -1;
    }

    @Override
    public int maxWidth() {
        return maxWidth != null ? maxWidth : -1;
    }

    @Override
    public int horizontalOffset() {
        return horizontalOffset != null ? horizontalOffset : 0;
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 0;
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
