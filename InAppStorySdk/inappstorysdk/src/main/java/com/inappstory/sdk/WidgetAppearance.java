package com.inappstory.sdk;

import android.content.Context;
import android.graphics.Color;

import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.core.utils.network.annotations.models.Ignore;
import com.inappstory.sdk.core.utils.sharedpref.SharedPreferencesAPI;

public class WidgetAppearance {
    public Integer getTextColor() {
        if (textColor == null) return Color.WHITE;
        return textColor;
    }

    public void save() {
        try {
            if (!SharedPreferencesAPI.hasContext()) SharedPreferencesAPI.setContext(context);
            SharedPreferencesAPI.saveString("lastWidgetAppearance", JsonParser.getJson(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getCorners() {
        if (corners == null || corners < 0) return 0;
        return corners;
    }

    public void setRatio(Float ratio) {
        this.ratio = ratio;
    }

    Integer textColor;
    Integer corners;

    public Context getContext() {
        return context;
    }

    @Ignore
    Context context;

    public boolean isSandbox() {
        return sandbox;
    }

    boolean sandbox;

    public Float getRatio() {
        if (ratio == null || ratio <= 0) return 1f;
        return ratio;
    }

    Float ratio;

    public Class getWidgetClass() {
        return widgetClass;
    }

    @Ignore
    Class widgetClass;
}
