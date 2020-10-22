package io.casestory.sdk.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Constructor;
import java.util.HashMap;

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
        return headers;
    }

    private HashMap<String, String> headers;

    private static NetworkClient instance;

    public NetworkClient(Context context, String baseUrl, HashMap<String, String> headers) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.headers = headers;
        instance = this;
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
        if (instance == null) new NetworkClient.Builder().build();
        return instance;
    }

    private static Context appContext;

    public static void setContext(Context context) {
        appContext = context;
    }

    public static ApiInterface getApi() {
        if (instance == null) {
            new NetworkClient.Builder()
                    .context(appContext)
                    .baseUrl(ApiSettings.getInstance().getCmsUrl())
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", getUAString(appContext))
                    .addHeader("Authorization", "Bearer " + ApiSettings.getInstance().getCmsKey()).build();
        }
        if (apiInterface == null) {
            apiInterface = NetworkHandler.implement(ApiInterface.class);
        }
        return apiInterface;
    }




    public static String getDefaultUserAgentString(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                return NewApiWrapper.getDefaultUserAgent(context);
            } catch (Exception e) {
                return getDefaultUserStringOld(context);
            }
        } else {
            return getDefaultUserStringOld(context);
        }
    }

    public static String getUAString(Context context) {
        String userAgent = getDefaultUserAgentString(context);
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

    private static String getDefaultUserStringOld(Context context) {
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


    @TargetApi(17)
    static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }

}
