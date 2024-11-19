package com.inappstory.sdk.inappmessage.ui.appearance.impl;


import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;

public class InAppMessageFullscreenSettings implements InAppMessageFullscreenAppearance {
    public String backgroundColor;

    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
