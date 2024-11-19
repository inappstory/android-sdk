package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;

public class InAppMessageBottomSheetSettings implements InAppMessageBottomSheetAppearance {
    public InAppMessageBottomSheetSettings(
            Integer maxHeight,
            Integer cornerRadius,
            String backgroundColor
    ) {
        this.maxHeight = maxHeight;
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
    }

    public Integer maxHeight;
    public Integer cornerRadius;
    public String backgroundColor;

    public InAppMessageBottomSheetSettings() {
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 32;
    }

    @Override
    public int maxHeight() {
        return maxHeight != null ? maxHeight : 600;
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
