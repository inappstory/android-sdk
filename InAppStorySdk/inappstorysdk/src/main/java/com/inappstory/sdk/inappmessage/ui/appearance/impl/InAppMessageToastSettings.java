package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageToastAppearance;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessageToastSettings implements InAppMessageToastAppearance {

    public InAppMessageToastSettings() {
    }

    public InAppMessageToastSettings(Map<String, Object> appearance, boolean disableClose) {
        if (appearance == null) return;
        this.disableClose = disableClose;
        String contentRatioKey = "content_ratio";
        String cornerRadiusKey = "corner_radius";
        String horizontalPaddingKey = "horizontal_offset";
        String verticalPaddingKey = "vertical_offset";
        String verticalPositionKey = "vertical_position";
        String horizontalPositionKey = "horizontal_position";
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
            horizontalOffset = numberUtils.convertNumberToInt(appearance.get(horizontalPaddingKey));
        }
        if (appearance.containsKey(verticalPaddingKey)) {
            verticalOffset = numberUtils.convertNumberToInt(appearance.get(verticalPaddingKey));
        }
        if (appearance.containsKey(verticalPositionKey)) {
            verticalPosition = numberUtils.convertNumberToInt(appearance.get(verticalPositionKey));
        }
        if (appearance.containsKey(horizontalPositionKey)) {
            horizontalPosition = numberUtils.convertNumberToInt(appearance.get(horizontalPositionKey));
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
        horizontalOffset = 16;
        verticalOffset = 16;
    }

    private Float contentRatio;
    private Integer cornerRadius;
    private Integer horizontalOffset;
    private Integer verticalOffset;
    private Integer closeButtonPosition;
    private Integer animationType;
    private String backgroundColor;
    private IReaderBackground background;
    private int verticalPosition = 0; //0 - bottom, 1 - top
    private Integer horizontalPosition = 1; //0 - start, 1 - center, 2 - end
    private boolean disableClose;
    private Map<String, Object> cardAppearance;


    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : 6.4f;
    }

    @Override
    public int horizontalPosition() {
        return horizontalPosition != null ? horizontalPosition : 1;
    }

    @Override
    public int verticalPosition() {
        return verticalPosition;
    }

    @Override
    public int horizontalOffset() {
        return horizontalOffset != null ? horizontalOffset : 16;
    }

    @Override
    public int verticalOffset() {
        return verticalOffset != null ? verticalOffset : 16;
    }

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 16;
    }

    @Override
    public int closeButtonPosition() {
        return closeButtonPosition != null ? closeButtonPosition : 0;
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

    @Override
    public Map<String, Object> cardAppearance() {
        return cardAppearance;
    }
}
