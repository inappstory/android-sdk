package com.inappstory.sdk.inappmessage.ui.appearance.impl;


import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;

public class InAppMessageFullscreenSettings implements InAppMessageFullscreenAppearance {
    public String backgroundColor;
    public Integer closeButtonPosition;
    public Integer animationType;

    public InAppMessageFullscreenSettings() {
    }

    public InAppMessageFullscreenSettings(
            String backgroundColor,
            Integer closeButtonPosition,
            Integer animationType
    ) {
        this.backgroundColor = backgroundColor;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 2;
    }

    @Override
    public int animationType() {
        return animationType != null ? animationType : 1;
    }

    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFFFF";
    }
}
