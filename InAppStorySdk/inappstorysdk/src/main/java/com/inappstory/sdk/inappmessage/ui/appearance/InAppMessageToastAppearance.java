package com.inappstory.sdk.inappmessage.ui.appearance;

public interface InAppMessageToastAppearance extends InAppMessageAppearance {
    float contentRatio();
    int position(); //TOP, BOTTOM
    int horizontalPadding();
    int verticalPadding();
    int cornerRadius();
    int closeButtonPosition();
    int animationType();
}
