package com.inappstory.sdk.stories.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;

/**
 * Created by Paperrose on 11.07.2018.
 */

public class Sizes {

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


    private static float getPixelScaleFactorExt(Context context) {
        if (context == null)
            return 1;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static Point getScreenSize(Context context) {
        if (context == null) {
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) context = inAppStoryManager.iasCore().appContext();
        }
        if (context == null)
            return new Point(0, 0);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int getFullPhoneHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        wm.getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getFullPhoneWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        wm.getDefaultDisplay().getRealMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int dpFloatToPxExt(float dp, Context context) {
        return Math.round(dp * getPixelScaleFactorExt(context));
    }

    public static boolean isTablet(Context context) {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        Context localContext = null;
        if (context == null) {
            if (inAppStoryManager != null)
                localContext = inAppStoryManager.iasCore().appContext();
        } else {
            localContext = context;
        }
        boolean isTablet = false;
        if (localContext != null) {
            isTablet = localContext.getResources().getBoolean(R.bool.isTablet);
            if (!isTablet) {
                Point size = Sizes.getScreenSize(localContext);
                float prop = 1f * size.y / size.x;
                if (prop < 16 / 9f) {
                    isTablet = true;
                }
            }
        }
        return isTablet;
    }

    public static int dpToPxExt(int dp, Context context) {
        return Math.round(dp * getPixelScaleFactorExt(context));
    }


    public static int pxToDpExt(int dp, Context context) {
        return Math.round(dp / getPixelScaleFactorExt(context));
    }
}

