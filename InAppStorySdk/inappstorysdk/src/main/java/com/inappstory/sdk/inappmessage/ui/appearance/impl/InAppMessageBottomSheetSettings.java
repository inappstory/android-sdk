package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBSLineAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;

public class InAppMessageBottomSheetSettings implements InAppMessageBottomSheetAppearance {
    public InAppMessageBottomSheetSettings(
            Float contentRatio,
            Integer cornerRadius,
            String backgroundColor,
            InAppMessageBSLineAppearance lineAppearance
    ) {
        this.contentRatio = contentRatio;
        this.lineAppearance = lineAppearance;
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
    }

    public Float contentRatio;
    public Integer cornerRadius;
    public String backgroundColor;
    public InAppMessageBSLineAppearance lineAppearance;

    public InAppMessageBottomSheetSettings() {
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 32;
    }

    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (2/3f);
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFFFF";
    }

    @Override
    public InAppMessageBSLineAppearance lineAppearance() {
        return lineAppearance != null ? lineAppearance : new InAppMessageBSLineSettings();
    }
}
