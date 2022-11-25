package com.inappstory.sdk.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.inappstory.sdk.BuildConfig;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;

import java.lang.reflect.Constructor;
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
        this.context = context;
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
            return Builder.this;
        }

        public Builder addHeader(String key, String header) {
            if (headers == null) headers = new HashMap<>();
            headers.put(key, header);
            return Builder.this;
        }

        public NetworkClient build() {
            return new NetworkClient(context, baseUrl, headers);
        }
    }

    public static NetworkClient getInstance() {
        if (instance == null) instance = new NetworkClient.Builder().build();
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
                if (ApiSettings.getInstance().getCmsUrl() == null) {
                    return new DumbApiInterface();
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
                        .baseUrl(ApiSettings.getInstance().getCmsUrl())
                        .addHeader("Accept", "application/json")
                        .addHeader("Accept-Language", language)
                        .addHeader("X-Device-Id", Settings.Secure.getString(appContext.getContentResolver(),
                                Settings.Secure.ANDROID_ID))
                        .addHeader("X-APP-PACKAGE-ID", packageName != null ? packageName : "-")
                        .addHeader("User-Agent", getUAString(appContext))
                        .addHeader("Authorization", "Bearer " + ApiSettings.getInstance().getApiKey()).build();
                apiInterface = null;
            }
            if (apiInterface == null) {
                apiInterface = NetworkHandler.implement(ApiInterface.class, instance);
            }
            return apiInterface;
        }
    }

    public static ApiInterface getStatApi() {
        if (statinstance == null) {
            statinstance = new NetworkClient.Builder()
                    .context(appContext)
                    .baseUrl(ApiSettings.getInstance().getCmsUrl())
                    .addHeader("User-Agent", getUAString(appContext)).build();
        }
        if (statApiInterface == null) {
            statApiInterface = NetworkHandler.implement(ApiInterface.class, statinstance);
        }
        return statApiInterface;
    }


    public static String getDefaultUserAgentString(Context context) {
        try {
            return NewApiWrapper.getDefaultUserAgent(context);
        } catch (Exception e) {
            return getDefaultUserStringOld(context);
        }
    }

    //Test
    public static String getUAString(Context context) {
        String userAgent = "";
        if (context == null) return "InAppStorySDK/" + BuildConfig.VERSION_CODE
                + " " + System.getProperty("http.agent");
        String agentString = System.getProperty("http.agent");
        if (agentString != null && !agentString.isEmpty()) {
            int appVersion = BuildConfig.VERSION_CODE;
            String appVersionName = BuildConfig.VERSION_NAME;
            String appPackageName = "";
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersion = pInfo.versionCode;
                appVersionName = pInfo.versionName;
                appPackageName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            userAgent = "InAppStorySDK/" + BuildConfig.VERSION_CODE
                    + " " + System.getProperty("http.agent") + " " + "Application/" + appVersion + " (" + appPackageName + " " + appVersionName + ")";
        } else {
            userAgent = getDefaultUserAgentString(context);
        }
        String finalUA = "";
        for (int i = 0; i < userAgent.length(); i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
            } else {
                finalUA += c;
            }
        }
        return finalUA;
    }

    //Test
    public static String getDefaultUserStringOld(Context context) {
        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                return settings.getUserAgentString();
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            try {

                return new WebView(context).getSettings().getUserAgentString();
            } catch (Exception e2) {
                return System.getProperty("http.agent");
            }
        }
    }

    static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }

}
