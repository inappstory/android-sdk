package com.inappstory.sdk.utils.ui;

import android.graphics.Color;
import android.os.Build;
import android.view.Window;

import com.inappstory.sdk.core.utils.ColorUtils;

public class SystemUiUtils {

    public static void setNavBarColor(int color, Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            window.setNavigationBarColor(color);
        }
    }

    public static void setStatusBarColor(int color, Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            window.setStatusBarColor(color);
        }
    }

    public static void modifyNabBarColor(int alpha, Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            window.setNavigationBarColor(ColorUtils.modifyAlpha(window.getStatusBarColor(), alpha));
        }
    }

    public static int getStatusBarColorAlpha(Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            return Color.alpha(window.getStatusBarColor());
        }
        return 255;
    }

    public static int getStatusBarColor(Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            return window.getStatusBarColor();
        }
        return Color.BLACK;
    }

    public static int getNavigationBarColorAlpha(Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            return Color.alpha(window.getNavigationBarColor());
        }
        return 255;
    }

    public static void modifyStatusBarColor(int alpha, Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            window.setStatusBarColor(ColorUtils.modifyAlpha(window.getStatusBarColor(), alpha));
        }
    }
}
