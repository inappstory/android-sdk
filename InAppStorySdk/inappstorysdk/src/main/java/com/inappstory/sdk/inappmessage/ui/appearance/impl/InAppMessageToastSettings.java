package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageToastAppearance;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessageToastSettings implements InAppMessageToastAppearance {

    public InAppMessageToastSettings() {}

    public InAppMessageToastSettings(Map<String, Object> appearance, boolean disableClose) {
        if (appearance == null) return;
        this.disableClose = disableClose;
        String contentRatioKey = "content_ratio";
        String cornerRadiusKey = "corner_radius";
        String horizontalPaddingKey = "horizontal_padding";
        String verticalPaddingKey = "vertical_padding";
        String positionKey = "position";
        String closeButtonPositionKey = "close_button_position";
        String animationTypeKey = "animation_type";
        String backgroundColorKey = "background_color";
        String backgroundKey = "background";
        String cardAppearanceKey = "card_appearance";
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
        if (appearance.containsKey(verticalPaddingKey)) {
            verticalPadding = numberUtils.convertNumberToInt(appearance.get(verticalPaddingKey));
        }
        if (appearance.containsKey(positionKey)) {
            position = numberUtils.convertNumberToInt(appearance.get(positionKey));
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
        if (appearance.containsKey(backgroundKey)) {
            background = new ReaderBackgroundSettings(
                    (Map<String, Object>) appearance.get(backgroundKey)
            );
        }
        if (appearance.containsKey(cardAppearanceKey)) {
            cardAppearance = (Map<String, Object>) appearance.get(cardAppearanceKey);
        }
    }

    private Float contentRatio;
    private Integer cornerRadius;
    private Integer horizontalPadding;
    private Integer verticalPadding;
    private Integer closeButtonPosition;
    private Integer animationType;
    private String backgroundColor;
    private InAppMessageBackdrop backdrop;
    private IReaderBackground background;
    private int position = 0; //0 - bottom, 1 - top
    private boolean disableClose;
    private Map<String, Object> cardAppearance;


    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (4 / 3f);
    }

    @Override
    public int position() {
        return position;
    }

    @Override
    public int horizontalPadding() {
        return horizontalPadding != null ? horizontalPadding : 16;
    }

    @Override
    public int verticalPadding() {
        return verticalPadding != null ? verticalPadding : 16;
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

    @Override
    public Map<String, Object> cardAppearance() {
        return cardAppearance;
    }
}
