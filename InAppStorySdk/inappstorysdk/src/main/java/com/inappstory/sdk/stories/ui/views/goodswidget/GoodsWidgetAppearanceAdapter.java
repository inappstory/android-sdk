package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.utils.Sizes;

public class GoodsWidgetAppearanceAdapter implements IGoodsWidgetAppearance {

    public Context context;

    public GoodsWidgetAppearanceAdapter() {
    }

    @Override
    public int getBackgroundHeight() {
        return Sizes.dpToPxExt(200, context);
    }

    @Override
    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public int getDimColor() {
        return Color.parseColor("#50000000");
    }

    @Override
    public Drawable getCloseButtonImage() {
        if (context != null)
            return ResourcesCompat.getDrawable(
                    context.getResources(),
                    R.drawable.cs_swipe_down_arrow,
                    context.getTheme()
            );
        return null;
    }

    @Override
    public int getCloseButtonColor() {
        return Color.parseColor("#737373");
    }
}
