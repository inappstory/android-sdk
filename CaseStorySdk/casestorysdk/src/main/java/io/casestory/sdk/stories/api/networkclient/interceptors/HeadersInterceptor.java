package io.casestory.sdk.stories.api.networkclient.interceptors;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Используется ApiClient при создании клиента, для взаимодействия с сервером.
 * Добавляет в запросы заголовки Accept, X-Application-Id и X-Session
 */

public class HeadersInterceptor implements Interceptor {

    private String key;

    public HeadersInterceptor(String key) {
        this.key = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request req = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + key)
                .addHeader("Cache-Control", "no-cache")
                .build();


        return chain.proceed(req);
    }
}
