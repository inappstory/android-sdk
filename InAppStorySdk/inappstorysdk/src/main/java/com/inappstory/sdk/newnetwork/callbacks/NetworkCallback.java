package com.inappstory.sdk.newnetwork.callbacks;

import com.inappstory.sdk.newnetwork.interfaces.NetworkErrorsHandler;
import com.inappstory.sdk.newnetwork.models.Response;


public abstract class NetworkCallback<T> implements Callback<T>, NetworkErrorsHandler {
    @Override
    public void onFailure(Response response) {
        onError(response.code, response.errorBody);
    }

    protected void errorDefault(String message) {

    }

    @Override
    public void onError(int code, String message) {
        switch (code) {
            case -1:
                timeout();
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

    @Override
    public void error400(String message) {

    }

    @Override
    public void error401(String message) {

    }

    @Override
    public void error402(String message) {

    }

    @Override
    public void error403(String message) {

    }

    @Override
    public void error404(String message) {

    }

    @Override
    public void error405(String message) {

    }

    @Override
    public void error409(String message) {

    }

    @Override
    public void error410(String message) {

    }

    @Override
    public void error415(String message) {

    }

    @Override
    public void error418(String message) {

    }

    @Override
    public void error422(String message) {

    }

    @Override
    public void error423(String message) {

    }

    @Override
    public void error424(String message) {

    }

    @Override
    public void error429(String message) {

    }

    @Override
    public void error500(String message) {

    }

    @Override
    public void error502(String message) {

    }

    @Override
    public void timeout() {

    }
}
