package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageUndefinedAppearance;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class InAppMessageUndefinedSettings implements InAppMessageUndefinedAppearance {


    public InAppMessageUndefinedSettings() {

    }

    @Override
    public String backgroundColor() {
        return "#FFFFFF";
    }

    @Override
    public IReaderBackground background() {
        return null;
    }

    @Override
    public Drawable backgroundDrawable() {
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(ColorUtils.parseColorRGBA(backgroundColor()));
        return drawable;
    }

    @Override
    public boolean disableClose() {
        return false;
    }

    @Override
    public Map<String, Object> cardAppearance() {
        return new HashMap<>();
    }
}
