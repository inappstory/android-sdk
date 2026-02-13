package com.inappstory.sdk.refactoring.core.network.headers;

import android.annotation.SuppressLint;

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
