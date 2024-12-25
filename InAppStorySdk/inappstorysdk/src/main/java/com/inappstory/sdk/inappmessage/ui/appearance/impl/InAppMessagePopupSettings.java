package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessagePopupSettings implements InAppMessagePopupAppearance {
    public InAppMessagePopupSettings(
            Float contentRatio,
            Integer cornerRadius,
            Integer horizontalPadding,
            Integer closeButtonPosition,
            Integer animationType,
            String backgroundColor
    ) {
        this.contentRatio = contentRatio;
        this.cornerRadius = cornerRadius;
        this.horizontalPadding = horizontalPadding;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
        this.backgroundColor = backgroundColor;
    }

    public InAppMessagePopupSettings() {}

    public InAppMessagePopupSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String contentRatioKey = "content_ratio";
        String cornerRadiusKey = "corner_radius";
        String horizontalPaddingKey = "horizontal_padding";
        String closeButtonPositionKey = "close_button_position";
        String animationTypeKey = "animation_type";
        String backgroundColorKey = "background_color";
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(contentRatioKey)) {
            contentRatio = numberUtils.convertNumberToFloat(appearance.get(contentRatioKey));
        }
        if (appearance.containsKey(cornerRadiusKey)) {
            cornerRadius = numberUtils.convertNumberToInt(appearance.get(cornerRadiusKey));
        }
        if (appearance.containsKey(horizontalPaddingKey)) {
            horizontalPadding = numberUtils.convertNumberToInt(appearance.get(horizontalPaddingKey));
        }
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

    private Float contentRatio;
    private Integer cornerRadius;
    private Integer horizontalPadding;
    private Integer closeButtonPosition;
    private Integer animationType;
    private String backgroundColor;

    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (2/3f);
    }

    @Override
    public int horizontalPadding() {
        return horizontalPadding != null ? horizontalPadding : 16;
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 16;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 2;
    }

    @Override
    public int animationType() {
        return animationType != null ? animationType : 2;
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
