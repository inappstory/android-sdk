package com.inappstory.sdk.inappmessage.ui.appearance;

public interface InAppMessageToastAppearance extends InAppMessageAppearance {
    float contentRatio();
    int horizontalPosition();
    int verticalPosition(); //TOP, BOTTOM
    int horizontalOffset();
    int verticalOffset();
    int cornerRadius();
    int closeButtonPosition();
    int animationType();
}
