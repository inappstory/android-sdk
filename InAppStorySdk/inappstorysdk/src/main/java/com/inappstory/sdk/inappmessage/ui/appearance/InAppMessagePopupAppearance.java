package com.inappstory.sdk.inappmessage.ui.appearance;

public interface InAppMessagePopupAppearance extends InAppMessageAppearance {
    float contentRatio();
    int horizontalPadding();
    int cornerRadius();
    int closeButtonPosition();
    int animationType();
    InAppMessageBackdrop backdrop();
}
