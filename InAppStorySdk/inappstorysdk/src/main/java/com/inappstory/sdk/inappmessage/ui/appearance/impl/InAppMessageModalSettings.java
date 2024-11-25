package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;

public class InAppMessageModalSettings implements InAppMessageModalAppearance {
    public InAppMessageModalSettings(
            Integer contentHeight,
            Integer cornerRadius,
            Integer horizontalPadding,
            Integer closeButtonPosition,
            Integer animationType,
            String backgroundColor
    ) {
        this.contentHeight = contentHeight;
        this.cornerRadius = cornerRadius;
        this.horizontalPadding = horizontalPadding;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
        this.backgroundColor = backgroundColor;
    }

    public InAppMessageModalSettings() {}

    public Integer contentHeight;
    public Integer cornerRadius;
    public Integer horizontalPadding;
    public Integer closeButtonPosition;
    public Integer animationType;
    public String backgroundColor;

    @Override
    public int contentHeight() {
        return contentHeight != null ? contentHeight : 600;
    }

    @Override
    public int horizontalPadding() {
        return horizontalPadding != null ? horizontalPadding : 16;
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 16;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 2;
    }

    @Override
    public int animationType() {
        return animationType != null ? animationType : 2;
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFFFF";
    }
}
