package io.casestory.sdk.network;

import java.util.HashMap;

public final class Response {
    public String body;
    public String errorBody;
    public HashMap<String, String> headers;
    public int code;

    Response(Builder builder) {
        this.code = builder.code;
        this.errorBody = builder.errorBody;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public static class Builder {
        public String body;
        public String errorBody;
        public HashMap<String, String> headers;
        public int code;

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder errorBody(String errorBody) {
            this.errorBody = errorBody;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder header(String key, String val) {
            if (headers == null) headers = new HashMap<>();
            headers.put(key, val);
            return this;
        }

        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}