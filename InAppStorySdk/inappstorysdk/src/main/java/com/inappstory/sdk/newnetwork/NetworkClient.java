package com.inappstory.sdk.newnetwork;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.inappstory.sdk.newnetwork.dummy.DummyApiInterface;
import com.inappstory.sdk.newnetwork.utils.UserAgent;

import java.util.HashMap;
import java.util.Locale;

public class NetworkClient {
    private static ApiInterface apiInterface;

    private Context context;

    private String baseUrl;

    public Context getContext() {
        return context;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HashMap<String, String> getHeaders() {
        if (context == null) return null;
        return headers;
    }

    private static NetworkClient instance;

    private NetworkClient(Context context, String baseUrl) {
        this.appContext = context.getApplicationContext();
        this.baseUrl = baseUrl;
    }

    public static class Builder {
        private Context context;
        private String baseUrl;

        public Builder baseUrl(String url) {
            this.baseUrl = url;
            return Builder.this;
        }

        public Builder context(Context context) {
            this.context = context.getApplicationContext();
            return this;
        }

        public NetworkClient build() {
            return new NetworkClient(context, baseUrl);
        }
    }

    public static NetworkClient getInstance() {
        if (instance == null) instance = (new Builder()).build();
        return instance;
    }

    private Context appContext;

    public static void clear() {
        instance = null;
        apiInterface = null;
    }

    private static Object syncLock = new Object();

    public static ApiInterface getApi() {
        synchronized (syncLock) {
            if (instance == null || instance.getBaseUrl() == null) {
                if (ApiSettings.getInstance().getHost() == null) {
                    return new DummyApiInterface();
                }
                instance = new Builder()
                        .context(appContext)
                        .baseUrl(ApiSettings.getInstance().getHost())
                        .build();
                apiInterface = null;
            }
            if (apiInterface == null) {
                apiInterface = NetworkHandler.implement(ApiInterface.class);
            }
            return apiInterface;
        }
    }


}
