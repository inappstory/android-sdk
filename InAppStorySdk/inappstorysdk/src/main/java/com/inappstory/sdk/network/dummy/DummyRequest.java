package com.inappstory.sdk.network.dummy;


import com.inappstory.sdk.network.callbacks.Callback;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.Response;

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