package com.inappstory.sdk.newnetwork.utils.headers;

import android.content.Context;
import android.util.Pair;

public class XAppPackageIdHeader implements Header {
    private Context appContext;

    public XAppPackageIdHeader(Context context) {
        if (context != null)
            this.appContext = context.getApplicationContext();
    }

    @Override
    public String getKey() {
        return "X-APP-PACKAGE-ID";
    }

    @Override
    public String getValue() {
        String packageName = null;
        if (appContext != null)
            packageName = appContext.getPackageName();
        return packageName != null ? packageName : "-";
    }
}
