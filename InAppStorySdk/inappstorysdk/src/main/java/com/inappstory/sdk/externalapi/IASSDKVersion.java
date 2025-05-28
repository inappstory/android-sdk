package com.inappstory.sdk.externalapi;

public class IASSDKVersion {
    public IASSDKVersion(String version, String apiVersion, int build) {
        this.version = version;
        this.apiVersion = apiVersion;
        this.build = build;
    }

    public String apiVersion;
    public String version;
    public int build;

    @Override
    public String toString() {
        return "IASSDKVersion{" +
                "apiVersion='" + apiVersion + '\'' +
                ", version='" + version + '\'' +
                ", build=" + build +
                '}';
    }
}
