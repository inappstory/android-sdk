package com.inappstory.sdk.inappmessage.ui.appearance;


public interface InAppMessageBottomSheetAppearance extends InAppMessageAppearance {
    int cornerRadius();
    float contentRatio();
    InAppMessageBSLineAppearance lineAppearance();
    InAppMessageBackdrop backdrop();
}
