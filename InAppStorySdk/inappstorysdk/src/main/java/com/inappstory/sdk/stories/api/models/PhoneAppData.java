package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.network.annotations.models.SerializedName;

public class PhoneAppData {
    public String platform;
    public String model;
    public String manufacturer;
    public String brand;
    @SerializedName("screen_width")
    public int screenWidth;
    @SerializedName("screen_height")
    public int screenHeight;
    @SerializedName("screen_dpi")
    public int screenDpi;
    @SerializedName("os_version")
    public String osVersion;
    @SerializedName("os_sdk_version")
    public int osSdkVersion;
    @SerializedName("app_package_id")
    public String appPackageId;
    @SerializedName("app_version")
    public String appVersion;
    @SerializedName("app_build")
    public int appBuild;
}
