package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;

public class InAppMessageModalSettings implements InAppMessageModalAppearance {
    public InAppMessageModalSettings(
            Float contentRatio,
            Integer cornerRadius,
            Integer horizontalPadding,
            Integer closeButtonPosition,
            Integer animationType,
            String backgroundColor
    ) {
        this.contentRatio = contentRatio;
        this.cornerRadius = cornerRadius;
        this.horizontalPadding = horizontalPadding;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
        this.backgroundColor = backgroundColor;
    }

    public InAppMessageModalSettings() {}

    public Float contentRatio;
    public Integer cornerRadius;
    public Integer horizontalPadding;
    public Integer closeButtonPosition;
    public Integer animationType;
    public String backgroundColor;

    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (2/3f);
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
