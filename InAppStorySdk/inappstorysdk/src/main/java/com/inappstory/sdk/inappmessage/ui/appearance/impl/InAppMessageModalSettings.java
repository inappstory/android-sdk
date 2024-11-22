package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;

public class InAppMessageModalSettings implements InAppMessageModalAppearance {
    public Integer contentHeight;
    public Integer cornerRadius;
    public Integer horizontalOffset;
    public Integer closeButtonPosition;
    public Integer animationType;
    public String backgroundColor;

    @Override
    public int contentHeight() {
        return contentHeight != null ? contentHeight : -1;
    }

    @Override
    public int horizontalPadding() {
        return horizontalOffset != null ? horizontalOffset : 0;
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 0;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 2;
    }

    @Override
    public int animationType() {
        return animationType != null ? contentHeight : -1;
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
