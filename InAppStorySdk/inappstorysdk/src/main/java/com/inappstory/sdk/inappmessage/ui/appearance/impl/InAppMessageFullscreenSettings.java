package com.inappstory.sdk.inappmessage.ui.appearance.impl;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessageFullscreenSettings implements InAppMessageFullscreenAppearance {
    private String backgroundColor;
    private Integer closeButtonPosition;
    private Integer animationType;
    private IReaderBackground background;
    private boolean disableClose;

    public InAppMessageFullscreenSettings() {
    }

    public InAppMessageFullscreenSettings(Map<String, Object> appearance, boolean disableClose) {
        if (appearance == null) return;
        this.disableClose = disableClose;
        String closeButtonPositionKey = "close_button_position";
        String animationTypeKey = "animation_type";
        String backgroundColorKey = "background_color";
        String backgroundKey = "background";
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(closeButtonPositionKey)) {
            closeButtonPosition = numberUtils.convertNumberToInt(appearance.get(closeButtonPositionKey));
        }
        if (appearance.containsKey(animationTypeKey)) {
            animationType = numberUtils.convertNumberToInt(appearance.get(animationTypeKey));
        }
        if (appearance.containsKey(backgroundColorKey)) {
            backgroundColor = (String) appearance.get(backgroundColorKey);
        }
        if (appearance.containsKey(backgroundKey)) {
            background = new ReaderBackgroundSettings(
                    (Map<String, Object>) appearance.get(backgroundKey)
            );
        }
    }


    public InAppMessageFullscreenSettings(
            String backgroundColor,
            Integer closeButtonPosition,
            Integer animationType
    ) {
        this.backgroundColor = backgroundColor;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 2;
    }

    @Override
    public int animationType() {
        return animationType != null ? animationType : 1;
    }



    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }

    @Override
    public IReaderBackground background() {
        return background;
    }

    @Override
    public Drawable backgroundDrawable() {
        if (background != null) return background.getBackgroundDrawable();
        ColorDrawable drawable = new ColorDrawable();
        drawable.setColor(ColorUtils.parseColorRGBA(backgroundColor()));
        return drawable;
    }

    @Override
    public boolean disableClose() {
        return disableClose;
    }

}
