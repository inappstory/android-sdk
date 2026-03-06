package com.inappstory.sdk.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    public static int getWebViewVersion(IASCore core) {
        try {
            //if (1 == 1) return 0;
            String versionName;
            PackageInfo packageInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                packageInfo = WebViewCompat.getCurrentWebViewPackage(core.appContext());
            } else {
                PackageManager pm = core.appContext().getPackageManager();
                try {
                    packageInfo = pm.getPackageInfo("com.google.android.webview", 0);
                } catch (PackageManager.NameNotFoundException e) {
                    return 0;
                }
            }
            if (packageInfo == null) return 0;
            versionName = packageInfo.versionName;
            String[] versions = versionName.split("\\.");
            return Integer.parseInt(versions[0]);
        } catch (Exception e) {
            return 0;
        }
    }
}
