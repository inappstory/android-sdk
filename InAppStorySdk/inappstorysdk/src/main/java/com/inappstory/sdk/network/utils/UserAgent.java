package com.inappstory.sdk.network.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.externalapi.ExternalPlatforms;

public class UserAgent {
    public String generate(IASCore core) {
        String userAgent = "";
        if (core == null)
            return ExternalPlatforms.NONE.getPrefix() + "/" + BuildConfig.VERSION_CODE
                    + " " + getSystemUA();
        String agentString = getSystemUA();
        Context context = core.appContext();
        String prefix = ((IASDataSettingsHolder) core.settingsAPI()).agentPrefix();
        if (!agentString.isEmpty()) {
            int appVersion = BuildConfig.VERSION_CODE;
            String appVersionName = BuildConfig.VERSION_NAME;
            String appPackageName = "";
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersion = pInfo.versionCode;
                appVersionName = pInfo.versionName;
                appPackageName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            IAppVersion externalAppVersion = ((IASDataSettingsHolder) core.settingsAPI()).externalAppVersion();
            if (externalAppVersion != null) {
                appVersionName = externalAppVersion.versionName();
                appVersion = externalAppVersion.versionBuild();
            }
            userAgent = prefix + "/" + BuildConfig.VERSION_CODE
                    + " " + agentString + " " + "Application/" + appVersion + " (" + appPackageName + " " + appVersionName + ")";
        } else {
            userAgent = getDefaultUserAgentString(context);
        }
        StringBuilder finalUA = new StringBuilder();
        for (int i = 0; i < userAgent.length(); i++) {
            char c = userAgent.charAt(i);
            if (c > '\u001f' && c < '\u007f') {
                finalUA.append(c);
            }
        }
        return finalUA.toString();
    }

    public String generate(Context context) {
        String userAgent = "";
        if (context == null)
            return ExternalPlatforms.NONE.getPrefix() + "/" + BuildConfig.VERSION_CODE
                    + " " + getSystemUA();
        String agentString = getSystemUA();
        if (!agentString.isEmpty()) {
            int appVersion = BuildConfig.VERSION_CODE;
            String appVersionName = BuildConfig.VERSION_NAME;
            String appPackageName = "";
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersion = pInfo.versionCode;
                appVersionName = pInfo.versionName;
                appPackageName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            userAgent = "InAppStorySDK/" + BuildConfig.VERSION_CODE
                    + " " + agentString + " " + "Application/" + appVersion + " (" + appPackageName + " " + appVersionName + ")";
        } else {
            userAgent = getDefaultUserAgentString(context);
        }
        StringBuilder finalUA = new StringBuilder();
        for (int i = 0; i < userAgent.length(); i++) {
            char c = userAgent.charAt(i);
            if (c > '\u001f' && c < '\u007f') {
                finalUA.append(c);
            }
        }
        return finalUA.toString();
    }

    private String getSystemUA() {
        String res = System.getProperty("http.agent");
        return (res != null) ? res.trim() : "";
    }

    private String getDefaultUserStringOld(Context context) {
        try {
            return new WebView(context).getSettings().getUserAgentString();
        } catch (Exception e) {
            return getSystemUA();
        }
    }

    private String getDefaultUserAgentString(Context context) {
        try {
            return WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            return getDefaultUserStringOld(context);
        }
    }
}
