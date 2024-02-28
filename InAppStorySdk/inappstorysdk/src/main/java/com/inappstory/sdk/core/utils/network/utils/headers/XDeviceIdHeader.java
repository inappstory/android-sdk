package com.inappstory.sdk.core.utils.network.utils.headers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class XDeviceIdHeader implements Header {
    private Context appContext;

    public XDeviceIdHeader(Context context) {
        if (context != null)
            this.appContext = context.getApplicationContext();
    }


    @Override
    public String getKey() {
        return HeadersKeys.DEVICE_ID;
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getValue() {
        String deviceId = null;
        if (appContext != null)
            deviceId = Settings.Secure.getString(
                    appContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
        return deviceId != null ? deviceId : "-";
    }
}