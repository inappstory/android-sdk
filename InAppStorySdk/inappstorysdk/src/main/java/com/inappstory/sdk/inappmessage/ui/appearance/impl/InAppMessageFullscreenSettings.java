package com.inappstory.sdk.inappmessage.ui.appearance.impl;


import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.utils.format.NumberUtils;

import java.util.Map;

public class InAppMessageFullscreenSettings implements InAppMessageFullscreenAppearance {
    private String backgroundColor;
    private Integer closeButtonPosition;
    private Integer animationType;

    public InAppMessageFullscreenSettings() {
    }

    public InAppMessageFullscreenSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String closeButtonPositionKey = "close_button_position";
        String animationTypeKey = "animation_type";
        String backgroundColorKey = "background_color";
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
}
