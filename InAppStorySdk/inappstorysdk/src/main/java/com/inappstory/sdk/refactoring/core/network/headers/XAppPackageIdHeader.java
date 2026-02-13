package com.inappstory.sdk.refactoring.core.network.headers;

import android.content.Context;

public class XAppPackageIdHeader implements Header {
    private Context appContext;

    public XAppPackageIdHeader(Context context) {
        if (context != null)
            this.appContext = context.getApplicationContext();
    }

    @Override
    public String getKey() {
        return HeadersKeys.APP_PACKAGE_ID;
    }

    @Override
    public String getValue() {
        String packageName = null;
        if (appContext != null)
            packageName = appContext.getPackageName();
        return packageName != null ? packageName : "-";
    }
}
