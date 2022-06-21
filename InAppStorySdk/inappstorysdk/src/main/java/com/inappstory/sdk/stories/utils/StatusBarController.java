package com.inappstory.sdk.stories.utils;

import android.app.Activity;
import android.view.View;

/**
 * Created by Paperrose on 23.07.2018.
 */

public class StatusBarController {
    public static int systemUiFlags = -1;

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
        if (context == null) return;
        View decorView = context.getWindow().getDecorView();
        // Show Status Bar.
        if (systemUiFlags != -1) {
            decorView.setSystemUiVisibility(systemUiFlags);
        } else {
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
