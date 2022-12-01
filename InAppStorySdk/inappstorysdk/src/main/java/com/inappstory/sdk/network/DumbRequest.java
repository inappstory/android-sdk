package com.inappstory.sdk.network;


public class DumbRequest<T> extends Request<T> {
    DumbRequest(Builder builder) {
        super(builder);
    }

    DumbRequest() {
        super(new Builder());
    }

    public Response execute() {
        return new Response.Builder().code(-99).errorBody("").build();
    }

    public void enqueue(final Callback callback) {
    }
}