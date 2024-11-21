package com.inappstory.sdk.network;

public class AppVersion {
    public String versionName() {
        return versionName;
    }

    public int versionBuild() {
        return versionBuild;
    }

    private final String versionName;
    private final int versionBuild;

    public AppVersion(String versionName, int versionBuild) {
        this.versionName = versionName;
        this.versionBuild = versionBuild;
    }
}
