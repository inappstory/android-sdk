package com.inappstory.sdk.inappmessage.ui.appearance;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public interface InAppMessageAppearance extends Serializable {
    String backgroundColor();
    InAppMessageBackground background();
    Drawable backgroundDrawable();
}
