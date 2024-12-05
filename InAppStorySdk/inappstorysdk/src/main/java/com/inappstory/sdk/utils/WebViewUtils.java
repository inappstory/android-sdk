package com.inappstory.sdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.webkit.CookieManager;

import androidx.webkit.WebViewCompat;

public class WebViewUtils {
    public static boolean isWebViewEnabled(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PackageInfo packageInfo = WebViewCompat.getCurrentWebViewPackage(context);
                return packageInfo != null;
            } else {
                CookieManager.getInstance();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
