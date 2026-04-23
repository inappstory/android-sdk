package com.inappstory.sdk.stories.utils;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Paperrose on 23.07.2018.
 */

public class StatusBarController {
    public static int systemUiFlags = -1;

    public static void showFullscreen(Activity context) {
        if (context == null) return;
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        /*int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);*/
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
        // Show Status Bar.

        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
