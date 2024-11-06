package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;

public class InAppMessageBottomSheetSettings implements InAppMessageBottomSheetAppearance {
    public Integer maxHeight;
    public Integer cornerRadius;

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 0;
    }

    @Override
    public int maxHeight() {
        return maxHeight != null ? maxHeight : -1;
    }
}
