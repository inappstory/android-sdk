package com.inappstory.sdk.inappmessage.ui.appearance;

import android.graphics.drawable.Drawable;

public interface InAppMessageBackground {
    boolean isTransparent();
    Drawable getBackgroundDrawable();
    String solid();
    InAppMessageLinearGradientBackground linearGradient();
}
