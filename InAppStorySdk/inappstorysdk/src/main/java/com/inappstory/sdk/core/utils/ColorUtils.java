package com.inappstory.sdk.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.palette.graphics.Palette;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Utility methods for working with colors.
 */
public class ColorUtils {

    private ColorUtils() {
    }

    public static final int IS_LIGHT = 0;
    public static final int IS_DARK = 1;
    public static final int LIGHTNESS_UNKNOWN = 2;

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult
    @ColorInt
    int modifyAlpha(@ColorInt int color,
                    @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult
    @ColorInt int modifyAlpha(@ColorInt int color,
                              @FloatRange(from = 0f, to = 1f) float alpha) {
        return modifyAlpha(color, (int) (255f * alpha));
    }

    /**
     * Checks if the most populous color in the given palette is dark
     * <p/>
     * Annoyingly we have to return this Lightness 'enum' rather than a boolean as palette isn't
     * guaranteed to find the most populous color.
     */
    public static @Lightness int isDark(Palette palette) {
        Palette.Swatch mostPopulous = getMostPopulousSwatch(palette);
        if (mostPopulous == null) return LIGHTNESS_UNKNOWN;
        return isDark(mostPopulous.getRgb()) ? IS_DARK : IS_LIGHT;
    }

    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

    /**
     * Determines if a given bitmap is dark. This extracts a palette inline so should not be called
     * with a large image!!
     * <p/>
     * Note: If palette fails then check the color of the central pixel
     */
    public static boolean isDark(@NonNull Bitmap bitmap) {
        return isDark(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
    }

    /**
     * Determines if a given bitmap is dark. This extracts a palette inline so should not be called
     * with a large image!! If palette fails then check the color of the specified pixel
     */
    public static boolean isDark(@NonNull Bitmap bitmap, int backupPixelX, int backupPixelY) {
        // first try palette with a small color quant size
        Palette palette = Palette.from(bitmap).maximumColorCount(3).generate();
        if (palette != null && palette.getSwatches().size() > 0) {
            return isDark(palette) == IS_DARK;
        } else {
            // if palette failed, then check the color of the specified pixel
            return isDark(bitmap.getPixel(backupPixelX, backupPixelY));
        }
    }

    /**
     * Check if a color is dark (convert to XYZ & check Y component)
     */
    public static boolean isDark(@ColorInt int color) {

        return androidx.core.graphics.ColorUtils.calculateLuminance(color) < 0.5;
    }

    /**
     * Calculate a variant of the color to make it more suitable for overlaying information. Light
     * colors will be lightened and dark colors will be darkened
     *
     * @param color               the color to adjust
     * @param isDark              whether {@code color} is light or dark
     * @param lightnessMultiplier the amount to modify the color e.g. 0.1f will alter it by 10%
     * @return the adjusted color
     */
    public static @ColorInt int scrimify(@ColorInt int color,
                                         boolean isDark,
                                         @FloatRange(from = 0f, to = 1f) float lightnessMultiplier) {
        float[] hsl = new float[3];
        androidx.core.graphics.ColorUtils.colorToHSL(color, hsl);

        if (!isDark) {
            lightnessMultiplier += 1f;
        } else {
            lightnessMultiplier = 1f - lightnessMultiplier;
        }

        hsl[2] = MathUtils.clamp(hsl[2] * lightnessMultiplier, 0f, 1f);
        return androidx.core.graphics.ColorUtils.HSLToColor(hsl);
    }

    public static @ColorInt int scrimify(@ColorInt int color,
                                         @FloatRange(from = 0f, to = 1f) float lightnessMultiplier) {
        return scrimify(color, isDark(color), lightnessMultiplier);
    }

    /**
     * Queries the theme of the given {@code context} for a theme color.
     *
     * @param context            the context holding the current theme.
     * @param attrResId          the theme color attribute to resolve.
     * @param fallbackColorResId a color resource id tto fallback to if the theme color cannot be
     *                           resolved.
     * @return the theme color or the fallback color.
     */
    @ColorInt
    public static int getThemeColor(@NonNull Context context, @AttrRes int attrResId,
                                    @ColorRes int fallbackColorResId) {
        final TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(attrResId, tv, true)) {
            return tv.data;
        }
        return ContextCompat.getColor(context, fallbackColorResId);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IS_LIGHT, IS_DARK, LIGHTNESS_UNKNOWN})
    public @interface Lightness {
    }

    public static int parseColorRGBA(String hex) {
        if (hex.length() < 9)
            return Color.parseColor(hex);
        else {
            String rgbaHex = "#" + hex.substring(7, 9) + hex.substring(1, 7);
            return Color.parseColor(rgbaHex);
        }
    }



    public static double getColorsContrast(int color1, int color2) {
        double bright1 = getColorBright(color1);
        double bright2 = getColorBright(color2);
        double maxBright = Math.max(bright1, bright2) + 0.05f;
        double minBright = Math.min(bright1, bright2) + 0.05f;
        return maxBright / minBright;
    }

    public static double getColorBright(int rgb) {
        double rsRGB = ((rgb >> 16) & 0xff) / 255.0;  // extract red
        double gsRGB = ((rgb >> 8) & 0xff) / 255.0;  // extract green
        double bsRGB = ((rgb) & 0xff) / 255.0;  // extract blue
        double c1 = 0.03928;
        double c2 = 12.92;
        double c3 = 0.055;
        double c4 = 1.055;
        double c5 = 2.4;
        double r = (rsRGB < c1) ? rsRGB / c2 : Math.pow((rsRGB + c3) / c4, c5);
        double g = (gsRGB < c1) ? gsRGB / c2 : Math.pow((gsRGB + c3) / c4, c5);
        double b = (bsRGB < c1) ? bsRGB / c2 : Math.pow((bsRGB + c3) / c4, c5);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
}