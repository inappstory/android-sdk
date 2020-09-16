package io.casestory.sdk.stories.api.networkclient;

import android.util.Log;


import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Paperrose on 11.07.2018.
 */

public abstract class RetrofitCallback<T> implements Callback<T> {
    public abstract void onSuccess(T response);

    public static ArrayList<Integer> successCodes = new ArrayList<Integer>() {{
        add(200);
        add(201);
        add(202);
    }};

    public void releaseUi() {

    }

    public void onTimeout() {
        onError(-1, "Network timeout");
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            onTimeout();
        } else if (!(t instanceof SocketException)) {
            Log.e("sdkfailure", t.getMessage());
            onError(0, t.getMessage());
        }
        releaseUi();
    }

    protected void errorDefault(String message) {

    }

    protected void error400(String message) {

    }

    protected void error401(String message) {

    }

    protected void error403(String message) {

    }

    protected void error404(String message) {

    }

    protected void error405(String message) {

    }

    protected void error409(String message) {

    }

    protected void error410(String message) {

    }

    protected void error415(String message) {

    }

    protected void error418(String message) {

    }

    protected void error422(String message) {

    }

    protected void error423(String message) {

    }

    protected void error424(String message) {
     //   if (!StoriesManager.openProcess)
     //       StoriesManager.openStatistic(null);
    }

    protected void error429(String message) {

    }

    protected void error500(String message) {

    }

    protected void error502(String message) {

    }

    public void onError(int code, String message) {
        switch (code) {
            case 400:
                error400(message);
                break;
            case 401:
                error401(message);
                break;
            case 403:
                error403(message);
                break;
            case 404:
                error404(message);
                break;
            case 405:
                error405(message);
                break;
            case 409:
                error409(message);
                break;
            case 410:
                error410(message);
                break;
            case 415:
                error415(message);
                break;
            case 418:
                error418(message);
                break;
            case 422:
                error422(message);
                break;
            case 423:
                error423(message);
                break;
            case 424:
                error424(message);
                break;
            case 429:
                error429(message);
                break;
            case 500:
                error500(message);
                break;
            case 502:
                error502(message);
                break;
            default:
                errorDefault(message);
                break;

        }
    }


    @Override
    public final void onResponse(Call<T> call, Response<T> response) {

        if (successCodes.contains(response.raw().code())) {
            onSuccess(response.body());
        } else {
            onError(response.raw().code(), response.raw().message());
        }
        releaseUi();
    }

}
