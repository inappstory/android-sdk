package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBSLineAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;

public class InAppMessageBottomSheetSettings implements InAppMessageBottomSheetAppearance {
    public InAppMessageBottomSheetSettings(
            Integer contentHeight,
            Integer cornerRadius,
            String backgroundColor,
            InAppMessageBSLineAppearance lineAppearance
    ) {
        this.contentHeight = contentHeight;
        this.lineAppearance = lineAppearance;
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
    }

    public Integer contentHeight;
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
    public int contentHeight() {
        return contentHeight != null ? contentHeight : 600;
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
