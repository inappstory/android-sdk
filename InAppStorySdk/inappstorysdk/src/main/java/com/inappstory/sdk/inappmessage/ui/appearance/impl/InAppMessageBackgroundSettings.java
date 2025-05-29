package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackground;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageLinearGradientBackground;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.Map;

public class InAppMessageBackgroundSettings implements InAppMessageBackground {

    private float alpha = 1f;
    private String solid;
    private InAppMessageLinearGradientSettings linearGradient;

    public InAppMessageBackgroundSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String alphaKey = "alpha";
        String solidKey = "solid";
        String linearGradientKey = "linear_gradient";
        if (appearance.containsKey(alphaKey)) {
            alpha = new NumberUtils().convertNumberToFloat(appearance.get(alphaKey));
        }
        if (appearance.containsKey(linearGradientKey)) {
            linearGradient = new InAppMessageLinearGradientSettings(
                    (Map<String, Object>) appearance.get(linearGradientKey)
            );
        } else if (appearance.containsKey(solidKey)) {
            solid = (String) appearance.get(solidKey);
        }
    }

    public InAppMessageBackgroundSettings() {
    }

    @Override
    public float alpha() {
        return alpha;
    }

    @Override
    public Drawable getBackgroundDrawable() {
        Drawable drawable = null;
        if (linearGradient != null) {
            ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {
                    return linearGradient.gradientValue(width, height);
                }
            };
            drawable = new PaintDrawable();
            ((PaintDrawable) drawable).setShape(new RectShape());
            ((PaintDrawable) drawable).setShaderFactory(sf);
        } else if (solid != null) {
            drawable = new ColorDrawable();
            ((ColorDrawable) drawable).setColor(ColorUtils.parseColorRGBA(solid));
        } else {
            drawable = new ColorDrawable();
            ((ColorDrawable) drawable).setColor(Color.WHITE);
        }
        return drawable;
    }
}
