package com.inappstory.sdk;

import android.graphics.Color;

public class WidgetAppearance {
    public Integer getTextColor() {
        if (textColor == null) return Color.WHITE;
        return textColor;
    }

    public Integer getCorners() {
        if (corners == null || corners < 0) return 0;
        return corners;
    }

    Integer textColor;
    Integer corners;

    public Class getWidgetClass() {
        return widgetClass;
    }

    Class widgetClass;
}
