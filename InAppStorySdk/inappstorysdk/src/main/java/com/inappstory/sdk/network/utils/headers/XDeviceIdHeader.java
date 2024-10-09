package com.inappstory.sdk.network.utils.headers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.inappstory.sdk.InAppStoryManager;

public class XDeviceIdHeader implements Header {
    private final String deviceId;

    public XDeviceIdHeader(String deviceId) {
        this.deviceId = deviceId;
    }


    @Override
    public String getKey() {
        return HeadersKeys.DEVICE_ID;
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getValue() {
        return deviceId != null ? deviceId : "-";
    }
}
