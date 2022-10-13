package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.SerializedName;

public class SessionOpenObject {
    @SerializedName("features")
    String features;
    @SerializedName("platform")
    String platform;
    @SerializedName("device_id")
    String deviceId;
    @SerializedName("model")
    String model;
    @SerializedName("manufacturer")
    String manufacturer;
    @SerializedName("brand")
    String brand;
    @SerializedName("screen_width")
    String screenWidth;
    @SerializedName("screen_height")
    String screenHeight;
    @SerializedName("screen_dpi")
    String screenDpi;
    @SerializedName("os_version")
    String osVersion;
    @SerializedName("os_sdk_version")
    String osSdkVersion;
    @SerializedName("app_package_id")
    String appPackageId;
    @SerializedName("app_version")
    String appVersion;
    @SerializedName("app_build")
    String appBuild;
    @SerializedName("user_id")
    String userId;

    public SessionOpenObject(String features, String platform,
                             String deviceId, String model,
                             String manufacturer, String brand,
                             String screenWidth, String screenHeight,
                             String screenDpi, String osVersion,
                             String osSdkVersion, String appPackageId,
                             String appVersion, String appBuild,
                             String userId) {
        this.features = features;
        this.platform = platform;
        this.deviceId = deviceId;
        this.model = model;
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenDpi = screenDpi;
        this.osVersion = osVersion;
        this.osSdkVersion = osSdkVersion;
        this.appPackageId = appPackageId;
        this.appVersion = appVersion;
        this.appBuild = appBuild;
        this.userId = userId;
    }
}
