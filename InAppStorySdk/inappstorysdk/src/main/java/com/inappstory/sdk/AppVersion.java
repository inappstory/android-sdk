package com.inappstory.sdk;

import com.inappstory.sdk.core.data.IAppVersion;
import com.inappstory.sdk.utils.UrlEncoder;

public class AppVersion implements IAppVersion {
    @Override
    public String versionName() {
        return new UrlEncoder().encode(versionName);
    }

    @Override
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
