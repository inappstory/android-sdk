package com.inappstory.sdk.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;
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

    public static PackageInfo getUncheckableWebViewPI(IASCore core) {
        PackageInfo packageInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            packageInfo = WebViewCompat.getCurrentWebViewPackage(core.appContext());
        }
        return packageInfo;
    }

    public static PackageInfo getCheckableWebViewPI(IASCore core) {
        try {
            String [] allowedPackages = {
                    "com.android.webview",
                    "com.google.android.webview",
                    "com.android.chrome"
            };
            PackageInfo packageInfo = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                packageInfo = WebViewCompat.getCurrentWebViewPackage(core.appContext());
            } else {
                PackageManager pm = core.appContext().getPackageManager();
                try {
                    for (String packageName : allowedPackages) {
                        packageInfo = pm.getPackageInfo(packageName, 0);
                        if (packageInfo != null) break;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return null;
                }
            }
            if (packageInfo == null) return null;
            for (String packageName : allowedPackages) {
                if (packageInfo.packageName.equals(packageName))
                    return packageInfo;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getWebViewVersion(@NonNull PackageInfo packageInfo) {
        try {
            String versionName;
            versionName = packageInfo.versionName;
            String[] versions = versionName.split("\\.");
            return Integer.parseInt(versions[0]);
        } catch (Exception e) {
            return 0;
        }
    }
}
