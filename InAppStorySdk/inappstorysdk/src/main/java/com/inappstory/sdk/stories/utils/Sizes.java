package com.inappstory.sdk.stories.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;

/**
 * Created by Paperrose on 11.07.2018.
 */

public class Sizes {
    @SuppressLint("InternalInsetResource")
    public static int getStatusBarHeight(Context context) {
        if (context == null) return 60;
        int result = 0;
         int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        } else {
            result = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * context.getResources().getDisplayMetrics().density);
        }
        return result;
    }


    public static float getPixelScaleFactorExt() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getContext() == null) return 1;
        DisplayMetrics displayMetrics = service.getContext().getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static float getPixelScaleFactorExt(Context context) {
        if (context == null)
            return 1;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static Point getScreenSize() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getContext() == null) return new Point(0, 0);
        WindowManager wm = (WindowManager) service.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getScreenSize(Context context) {
        if (context == null)
            return new Point(0, 0);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int dpFloatToPxExt(float dp, Context context) {
        return Math.round(dp * getPixelScaleFactorExt(context));
    }

    public static int dpToPxExt(int dp) {
        return Math.round(dp * getPixelScaleFactorExt());
    }

    public static boolean isTablet() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null || service.getContext() == null) return false;
        return service.getContext().getResources().getBoolean(R.bool.isTablet);
    }

    public static int dpToPxExt(int dp, Context context) {
        return Math.round(dp * getPixelScaleFactorExt(context));
    }

    public static int pxToDpExt(int dp) {
        return Math.round(dp / getPixelScaleFactorExt());
    }

    public static int pxToDpExt(int dp, Context context) {
        return Math.round(dp / getPixelScaleFactorExt(context));
    }
}

