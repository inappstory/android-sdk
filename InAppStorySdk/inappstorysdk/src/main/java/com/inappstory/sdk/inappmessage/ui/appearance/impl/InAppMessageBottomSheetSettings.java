package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBSLineAppearance;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
import com.inappstory.sdk.inappmessage.ui.appearance.IReaderBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBottomSheetAppearance;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessageBottomSheetSettings implements InAppMessageBottomSheetAppearance {
    public InAppMessageBottomSheetSettings(
            Float contentRatio,
            Integer cornerRadius,
            String backgroundColor,
            InAppMessageBSLineAppearance lineAppearance,
            InAppMessageBackdrop backdrop
    ) {
        this.contentRatio = contentRatio;
        this.lineAppearance = lineAppearance;
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        this.backdrop = backdrop;
    }

    private Float contentRatio;
    private Integer cornerRadius;
    private String backgroundColor;
    private IReaderBackground background;
    private InAppMessageBSLineAppearance lineAppearance;
    private InAppMessageBackdrop backdrop;
    private boolean disableClose;

    public InAppMessageBottomSheetSettings() {
    }

    public InAppMessageBottomSheetSettings(Map<String, Object> appearance, boolean disableClose) {
        if (appearance == null) return;
        this.disableClose = disableClose;
        String contentRatioKey = "content_ratio";
        String cornerRadiusKey = "corner_radius";
        String lineAppearanceKey = "close_button";
        String backgroundColorKey = "background_color";
        String backgroundKey = "background";
        String backdropKey = "backdrop";
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(contentRatioKey)) {
            contentRatio = numberUtils.convertNumberToFloat(appearance.get(contentRatioKey));
        }
        if (appearance.containsKey(cornerRadiusKey)) {
            cornerRadius = numberUtils.convertNumberToInt(appearance.get(cornerRadiusKey));
        }
        if (appearance.containsKey(lineAppearanceKey)) {
            lineAppearance = new InAppMessageBSLineSettings((Map<String, Object>) appearance.get(lineAppearanceKey));
        }
        if (appearance.containsKey(backdropKey)) {
            backdrop = new InAppMessageBackdropSettings((Map<String, Object>) appearance.get(backdropKey));
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

    @Override
    public int cornerRadius() {
        return cornerRadius != null ? cornerRadius : 16;
    }

    @Override
    public float contentRatio() {
        return contentRatio != null ? contentRatio : (4 / 3f);
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
    public InAppMessageBSLineAppearance lineAppearance() {
        return lineAppearance != null ? lineAppearance : new InAppMessageBSLineSettings();
    }

    @Override
    public InAppMessageBackdrop backdrop() {
        return backdrop != null ? backdrop : new InAppMessageBackdropSettings();
    }
}
