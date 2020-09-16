package io.casestory.sdk.stories.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import io.casestory.sdk.CaseStoryManager;

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


    public static float getPixelScaleFactorExt() {
        if (CaseStoryManager.getInstance() == null) return 1;
        Context con = CaseStoryManager.getInstance().getContext();
        if (con == null)
            return 1;
        DisplayMetrics displayMetrics = con.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static float getPixelScaleFactorExt(Context context) {
        Context con = context;
        if (con == null)
            return 1;
        DisplayMetrics displayMetrics = con.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    public static Point getScreenSize() {
        Context con = CaseStoryManager.getInstance().getContext();
        if (con == null) return new Point(0,0);
        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getScreenSize(Context context) {
        Context con = context;
        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int dpToPxExt(int dp) {
        return Math.round(dp * getPixelScaleFactorExt());
    }

    public static boolean isTablet() {
        return false;
    }

    public static int dpToPxExt(int dp, Context context) {
        return Math.round(dp * getPixelScaleFactorExt(context));
    }

    public static int pxToDpExt(int dp) {
        return Math.round(dp / getPixelScaleFactorExt());
    }
}

