package io.casestory.sdk.stories.api.networkclient.interceptors;


import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Используется ApiClient при создании клиента, для взаимодействия с сервером.
 * Повторяет запросы по 3 раза в случае неуспеха. (Изначально делали для Tele2)
 */

public class RepeatInterceptor implements Interceptor {

    public static ArrayList<Integer> noncheckedCodes = new ArrayList<Integer>() {{
        add(200);
        add(201);
        add(202);
        add(400);
        add(401);
        add(403);
        add(404);
        add(405);
        add(409);
        add(410);
        add(415);
        add(418);
        add(422);
        add(423);
        add(429);
        add(449);
        add(500);
    }};

    public RepeatInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int tryCount = 0;
        while (!response.isSuccessful() && !noncheckedCodes.contains(response.code()) && tryCount < 1) {
            tryCount++;
            //Log.e("repeat request", Integer.toString(tryCount));
            response = chain.proceed(request);
        }
        return response;
    }
}
