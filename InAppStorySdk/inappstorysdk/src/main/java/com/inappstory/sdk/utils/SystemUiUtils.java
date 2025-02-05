package com.inappstory.sdk.utils;

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

    public static void modifyStatusBarColor(int alpha, Window window) {
        if (Build.VERSION.SDK_INT < 35) {
            window.setStatusBarColor(ColorUtils.modifyAlpha(window.getStatusBarColor(), alpha));
        }
    }
}
