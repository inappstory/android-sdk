package com.inappstory.sdk.packages.core.base.network.utils.headers;


public class XAppPackageIdHeader implements Header {
    private final String appPackageId;

    public XAppPackageIdHeader(String appPackageId) {
        this.appPackageId = (appPackageId != null ? appPackageId : "-");
    }

    @Override
    public String getKey() {
        return HeadersKeys.APP_PACKAGE_ID;
    }

    @Override
    public String getValue() {
        return appPackageId;
    }
}
