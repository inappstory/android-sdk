package io.casestory.sdk.stories.api.networkclient.interceptors;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.IOException;
import java.lang.reflect.Constructor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Используется ApiClient при создании клиента, для взаимодействия с сервером.
 * Добавляет в запрос заголовок User-Agent
 */

public class UserAgentInterceptor implements Interceptor {
    public final String userAgent;

    public UserAgentInterceptor(Context context) {
        this.userAgent = getDefaultUserAgentString(context);
    }

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (userAgent != null) {
            String finalUA = "";
            for (int i = 0; i < userAgent.length(); i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                } else {
                    finalUA += c;
                }
            }
            Request requestWithUserAgent = originRequest.newBuilder()
                    .header("User-Agent", finalUA)
                    .build();
            return chain.proceed(requestWithUserAgent);
        } else {
            return chain.proceed(originRequest);
        }
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

    @TargetApi(17)
    static class NewApiWrapper {
        static String getDefaultUserAgent(Context context) {
            return WebSettings.getDefaultUserAgent(context);
        }
    }
}
