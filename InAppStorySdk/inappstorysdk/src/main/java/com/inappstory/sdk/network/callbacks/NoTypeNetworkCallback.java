package com.inappstory.sdk.network.callbacks;

import com.inappstory.sdk.network.models.Response;

import java.lang.reflect.Type;

public abstract class NoTypeNetworkCallback implements Callback<Response> {
    @Override
    public void onEmptyContent() {

    }

    @Override
    public void onFailure(Response response) {

    }

    @Override
    public Type getType() {
        return null;
    }
}
