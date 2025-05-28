package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
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
            String backgroundColor,
            InAppMessageBackdrop backdrop
    ) {
        this.contentRatio = contentRatio;
        this.cornerRadius = cornerRadius;
        this.horizontalPadding = horizontalPadding;
        this.closeButtonPosition = closeButtonPosition;
        this.animationType = animationType;
        this.backgroundColor = backgroundColor;
        this.backdrop = backdrop;
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
        String backdropKey = "backdrop";
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
        if (appearance.containsKey(backdropKey)) {
            backdrop = new InAppMessageBackdropSettings((Map<String, Object>) appearance.get(backdropKey));
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
    private InAppMessageBackdrop backdrop;

    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (4 / 3f);
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
    public InAppMessageBackdrop backdrop() {
        return backdrop != null ? backdrop : new InAppMessageBackdropSettings();
    }


    @Override
    public String backgroundColor() {
        return backgroundColor != null ? backgroundColor : "#FFFFFF";
    }
}
