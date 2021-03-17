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

    public Float getRatio() {
        if (ratio == null || ratio <= 0) return 1f;
        return ratio;
    }

    Float ratio;

    public Class getWidgetClass() {
        return widgetClass;
    }

    Class widgetClass;
}
