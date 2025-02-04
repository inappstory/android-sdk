package com.inappstory.sdk.utils;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.webkit.CookieManager;

import androidx.webkit.WebViewCompat;

import com.inappstory.sdk.core.IASCore;

public class WebViewUtils {
    public static boolean isWebViewEnabled(IASCore core) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PackageInfo packageInfo = WebViewCompat.getCurrentWebViewPackage(core.appContext());
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
