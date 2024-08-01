package com.inappstory.sdk.packages.core.base.network;


import com.inappstory.sdk.packages.core.base.network.utils.Size;

public interface IContextDependentSettings {
    String deviceId();
    String packageId();
    String appVersion();
    String userAgent();
    int appBuild();
    Size screenSize();
    float screenDpi();
}
