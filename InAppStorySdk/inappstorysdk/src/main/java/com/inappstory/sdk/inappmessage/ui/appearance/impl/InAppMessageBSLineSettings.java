package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBSLineAppearance;
import com.inappstory.sdk.utils.format.NumberUtils;

import java.util.Map;

public class InAppMessageBSLineSettings implements InAppMessageBSLineAppearance {
    public InAppMessageBSLineSettings(
            Integer topMargin,
            Integer width,
            Integer height,
            String color
    ) {
        this.topMargin = topMargin;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public InAppMessageBSLineSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String topMarginKey = "top_margin";
        String widthKey = "width";
        String heightKey = "height";
        String colorKey = "color";
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(topMarginKey)) {
            topMargin = numberUtils.convertNumberToInt(appearance.get(topMarginKey));
        }
        if (appearance.containsKey(widthKey)) {
            width = numberUtils.convertNumberToInt(appearance.get(widthKey));
        }
        if (appearance.containsKey(heightKey)) {
            height = numberUtils.convertNumberToInt(appearance.get(heightKey));
        }
        if (appearance.containsKey(colorKey)) {
            color = (String) appearance.get(colorKey);
        }
    }

    public InAppMessageBSLineSettings() {}

    private Integer topMargin;
    private Integer width;
    private Integer height;
    private String color;

    @Override
    public int topMargin() {
        return topMargin != null ? topMargin : 8;
    }

    @Override
    public int width() {
        return width != null ? width : 32;
    }

    @Override
    public int height() {
        return height != null ? height : 4;
    }

    @Override
    public String color() {
        return color != null ? color : "#00000080";
    }
}
