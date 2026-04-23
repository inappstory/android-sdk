package com.inappstory.sdk.stories.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.inappstory.sdk.utils.SystemUiUtils;

/**
 * Created by Paperrose on 23.07.2018.
 */

public class StatusBarController {
    public static int systemUiFlags = -1;

    public static void showFullscreen(Activity context) {
        if (context == null) return;


    }

    public static void hideStatusBar(Activity context, boolean withSb) {
        if (context == null) return;
        View decorView = context.getWindow().getDecorView();
        // Hide Status Bar.
        systemUiFlags = decorView.getSystemUiVisibility();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (withSb)
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static void showStatusBar(Activity context) {
        restore(context);
    }

    public static void restore(Activity context) {
        if (context == null) return;

    }
}
