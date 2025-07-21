package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.ReaderLinearGradientBackground;
import com.inappstory.sdk.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReaderLinearGradientSettings implements ReaderLinearGradientBackground {
    List<Float> positions = new ArrayList<>();
    List<String> colors = new ArrayList<>();
    int angle = 0;

    public ReaderLinearGradientSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String angleKey = "angle";
        String colorStepListKey = "color_step_list";
        String colorStepColorKey = "color";
        String colorStepPositionKey = "position";
        if (!appearance.containsKey(angleKey)) return;
        List<Map<String, Object>> colorSteps;
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(angleKey)) {
            angle = numberUtils.convertNumberToInt(appearance.get(angleKey));
        }
        if (appearance.containsKey(colorStepListKey)) {
            colorSteps = (List<Map<String, Object>>) appearance.get(colorStepListKey);
            if (colorSteps != null) {
                for (Map<String, Object> value : colorSteps) {
                    if (value.containsKey(colorStepColorKey) && value.containsKey(colorStepPositionKey)) {
                        positions.add(numberUtils.convertNumberToFloat(value.get(colorStepPositionKey)));
                        colors.add((String) value.get(colorStepColorKey));
                    }
                }
            }
        }
        if (positions.isEmpty()) {
            positions.add(0f);
            positions.add(1f);
            colors.add("#000000");
            colors.add("#000000");
        }
        if (positions.size() < 2) {
            positions.add(1f);
            colors.add(colors.get(0));
        }
    }

    public ReaderLinearGradientSettings() {
    }

    private RectF getLine(int width, int height, int angle) {
        RectF rectF = new RectF();
        if (angle < 90) {
            rectF.top = height;
            rectF.left = 0f;
            if (angle == 0) {
                rectF.bottom = 0f;
                rectF.right = 0f;
                return rectF;
            }
            double tan = Math.tan(Math.toRadians(90 - angle));
            double calcHeight = width * tan;
            if (calcHeight > height) {
                rectF.bottom = 0f;
                float shift = (width - (float) (height / tan)) / 2f;
                rectF.right = width - shift;
                rectF.left = shift;
            } else {
                rectF.right = width;
                float shift = (height - (float) calcHeight) / 2f;
                rectF.top = height - shift;
                rectF.bottom = shift;
            }
        } else if (angle < 180) {
            rectF.top = 0f;
            rectF.left = 0f;
            if (angle == 90) {
                rectF.bottom = 0f;
                rectF.right = width;
                return rectF;
            }
            double tan = Math.tan(Math.toRadians(angle - 90));
            double calcHeight = width * tan;
            if (calcHeight > height) {
                rectF.bottom = height;
                float shift = (width - (float) (height / tan)) / 2f;
                rectF.right = width - shift;
                rectF.left = shift;
            } else {
                rectF.right = width;
                float shift = (height - (float) calcHeight) / 2f;
                rectF.bottom = height - shift;
                rectF.top = shift;
            }
        } else if (angle < 270) {
            rectF.top = 0f;
            rectF.left = width;
            if (angle == 180) {
                rectF.bottom = height;
                rectF.right = 0f;
                return rectF;
            }
            double tan = Math.tan(Math.toRadians(angle - 180));
            double calcWidth = height * tan;
            if (calcWidth > width) {
                float shift = (height - (float) (width / tan)) / 2f;
                rectF.bottom = height - shift;
                rectF.top = shift;
                rectF.right = 0f;
            } else {
                float shift = (width - (float) calcWidth) / 2f;
                rectF.right = shift;
                rectF.left = width - shift;
                rectF.bottom = height;
            }
        } else {
            rectF.top = height;
            rectF.left = width;
            if (angle == 270) {
                rectF.top = 0f;
                rectF.left = width;
                rectF.bottom = 0f;
                rectF.right = 0f;
                return rectF;
            }
            double tan = Math.tan(Math.toRadians(angle - 270));
            double calcHeight = width * tan;
            if (calcHeight > height) {
                rectF.bottom = 0f;
                float shift = (width - (float) (height / tan)) / 2f;
                rectF.right = shift;
                rectF.left = width - shift;
            } else {
                rectF.right = 0f;
                float shift = (height - (float) calcHeight) / 2f;
                rectF.bottom = shift;
                rectF.top = height - shift;
            }
        }
        return rectF;
    }

    @Override
    public LinearGradient gradientValue(int width, int height) {
        RectF rectF = getLine(width, height, angle % 360);
        float[] floatPositions = new float[this.positions.size()];
        int[] intColors = new int[this.colors.size()];
        for (int i = 0; i < positions.size(); i++) {
            floatPositions[i] = positions.get(i) / 100f;
            intColors[i] = ColorUtils.parseColorRGBA(colors.get(i));
        }

        return new LinearGradient(
                rectF.left,
                rectF.top,
                rectF.right,
                rectF.bottom,
                intColors,
                floatPositions,
                Shader.TileMode.CLAMP
        );
    }
}
