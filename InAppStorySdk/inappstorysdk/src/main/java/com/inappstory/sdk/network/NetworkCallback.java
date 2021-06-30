package com.inappstory.sdk.network;


import android.util.Log;

public abstract class NetworkCallback<T> implements Callback<T> {
    @Override
    public final void onFailure(Response response) {
        onError(response.code, response.errorBody);
    }

    protected void errorDefault(String message) {

    }

    protected void error400(String message) {

    }


    protected void error402(String message) {
        Log.e("InAppStory_Network",
                "Access was terminated. Check the management console for details.");
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

    }

    protected void error429(String message) {

    }

    protected void error500(String message) {

    }

    protected void error502(String message) {

    }

    public void onError(int code, String message) {
        switch (code) {
            case -1:
                onTimeout();
            case 400:
                error400(message);
                break;
            case 401:
                error401(message);
                break;
            case 402:
                error402(message);
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

    public void onTimeout() {

    }
}
