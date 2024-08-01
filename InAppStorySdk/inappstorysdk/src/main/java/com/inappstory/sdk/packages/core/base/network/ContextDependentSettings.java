package com.inappstory.sdk.packages.core.base.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.packages.core.base.network.utils.Size;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

public class ContextDependentSettings implements IContextDependentSettings {
    private final String deviceId;
    private final String packageId;
    private final String appVersion;
    private final String userAgent;
    private final int appBuild;
    private final Size screenSize;
    private final float screenDpi;

    public ContextDependentSettings(Context context, boolean isDeviceIdEnabled) {
        if (isDeviceIdEnabled) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        } else {
            deviceId = null;
        }
        screenSize = new Size(Sizes.getScreenSize(context).x, Sizes.getScreenSize(context).y);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenDpi = metrics.density * 160f;

        packageId = context.getPackageName();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {

        }
        appVersion = (pInfo != null ? pInfo.versionName : "");
        appBuild = (pInfo != null ? pInfo.versionCode : 0);
        userAgent = StringsUtils.getEscapedString(
                new UserAgent().generate(context)
        );
    }

    @Override
    public String deviceId() {
        return deviceId;
    }

    @Override
    public String packageId() {
        return packageId;
    }

    @Override
    public String appVersion() {
        return appVersion;
    }

    @Override
    public String userAgent() {
        return userAgent;
    }

    @Override
    public int appBuild() {
        return appBuild;
    }

    @Override
    public Size screenSize() {
        return screenSize;
    }

    @Override
    public float screenDpi() {
        return screenDpi;
    }
}
