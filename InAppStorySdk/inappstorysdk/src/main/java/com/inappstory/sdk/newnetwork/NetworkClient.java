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
    private static ApiInterface statApiInterface;

    private Context context;

    private String baseUrl;

    public Context getContext() {
        return context;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    private HashMap<String, String> headers;

    private static NetworkClient instance;
    private static NetworkClient statinstance;

    public NetworkClient(Context context, String baseUrl, HashMap<String, String> headers) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl;
        this.headers = headers;
    }

    public static class Builder {
        private Context context;
        private String baseUrl;
        private HashMap<String, String> headers;

        public Builder baseUrl(String url) {
            this.baseUrl = url;
            return Builder.this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder addHeader(String key, String header) {
            if (headers == null) headers = new HashMap<>();
            headers.put(key, header);
            return this;
        }

        public NetworkClient build() {
            return new NetworkClient(context, baseUrl, headers);
        }
    }

    public static NetworkClient getInstance() {
        if (instance == null) instance = (new Builder()).build();
        return instance;
    }


    public static Context getAppContext() {
        return appContext;
    }

    private static Context appContext;

    public static void setContext(Context context) {
        appContext = context;
    }

    public static void clear() {
        instance = null;
        statinstance = null;
        apiInterface = null;
    }

    private static Object syncLock = new Object();

    public static ApiInterface getApi() {
        synchronized (syncLock) {
            if (instance == null || instance.getBaseUrl() == null) {
                if (ApiSettings.getInstance().getHost() == null) {
                    return new DummyApiInterface();
                }
                String packageName = appContext.getPackageName();
                String language;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    language = Locale.getDefault().toLanguageTag();
                } else {
                    language = Locale.getDefault().getLanguage();
                }
                instance = new Builder()
                        .context(appContext)
                        .baseUrl(ApiSettings.getInstance().getHost())
                        .addHeader("Accept", "application/json")
                        .addHeader("Accept-Language", language)
                        .addHeader("X-Device-Id",
                                Settings.Secure.getString(appContext.getContentResolver(),
                                Settings.Secure.ANDROID_ID)
                        )
                        .addHeader("X-APP-PACKAGE-ID", packageName != null ? packageName : "-")
                        .addHeader("User-Agent", new UserAgent().generate(appContext))
                        .addHeader("Authorization", "Bearer " +
                                ApiSettings.getInstance().getApiKey()).build();
                apiInterface = null;
            }
            if (apiInterface == null) {
                apiInterface = NetworkHandler.implement(ApiInterface.class);
            }
            return apiInterface;
        }
    }

    public static ApiInterface getStatApi() {
        if (statinstance == null) {
            statinstance = new Builder()
                    .context(appContext)
                    .baseUrl(ApiSettings.getInstance().getHost())
                    .addHeader("User-Agent", new UserAgent().generate(appContext)).build();
        }
        if (statApiInterface == null) {
            statApiInterface = NetworkHandler.implement(ApiInterface.class);
        }
        return statApiInterface;
    }



}
