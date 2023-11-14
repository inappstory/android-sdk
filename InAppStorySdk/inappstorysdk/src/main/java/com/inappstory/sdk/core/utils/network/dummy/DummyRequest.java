package com.inappstory.sdk.core.utils.network.dummy;


import com.inappstory.sdk.core.utils.network.callbacks.Callback;
import com.inappstory.sdk.core.utils.network.models.Request;
import com.inappstory.sdk.core.utils.network.models.Response;

public class DummyRequest<T> extends Request<T> {
    DummyRequest(Builder builder) {
        super(builder);
    }

    DummyRequest() {
        super(new Builder());
    }

    public Response execute() {
        return new Response.Builder().code(-99).errorBody("Dummy api called").build();
    }

    public void enqueue(Callback callback) {
        callback.onFailure(execute());
    }
}