package com.inappstory.sdk.newnetwork.dummy;


import com.inappstory.sdk.network.Callback;
import com.inappstory.sdk.newnetwork.models.Request;
import com.inappstory.sdk.newnetwork.models.Response;

public class DummyRequest<T> extends Request<T> {
    DummyRequest(Builder builder) {
        super(builder);
    }

    DummyRequest() {
        super(new Builder());
    }

    public Response execute() {
        return new Response.Builder().code(-99).errorBody("").build();
    }

    public void enqueue(final Callback callback) {
    }
}