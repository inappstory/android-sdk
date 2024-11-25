package com.inappstory.sdk.network.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.network.AppVersion;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UserAgent {
    public String generate(Context context) {
        String userAgent = "";
        if (context == null) return "InAppStorySDK/" + BuildConfig.VERSION_CODE
                + " " + getSystemUA();
        String agentString = getSystemUA();
        if (!agentString.isEmpty()) {
            int appVersionBuild = BuildConfig.VERSION_CODE;
            String appVersionName = BuildConfig.VERSION_NAME;
            String appPackageName = "";
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersionBuild = pInfo.versionCode;
                appVersionName = pInfo.versionName;
                appPackageName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) {
                AppVersion version = inAppStoryManager.appVersion;
                if (version != null) {
                    appVersionBuild = version.versionBuild();
                    appVersionName = version.versionName();
                }
            }
            userAgent = "InAppStorySDK/" +
                    BuildConfig.VERSION_CODE + " " +
                    agentString + " " +
                    "Application/" +
                    appVersionBuild + " (" +
                    appPackageName + " " +
                    new String(
                            appVersionName.getBytes(StandardCharsets.ISO_8859_1),
                            StandardCharsets.ISO_8859_1
                    ) + ")";
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
