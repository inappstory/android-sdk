package com.inappstory.sdk.inappmessage.ui.appearance;


public interface InAppMessageBottomSheetAppearance extends InAppMessageAppearance {
    int cornerRadius();
    float contentRatio();
    String backgroundColor();
    InAppMessageBSLineAppearance lineAppearance();
}
