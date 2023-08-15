package com.inappstory.sdk.newnetwork.models;

import android.util.Pair;

import com.inappstory.sdk.newnetwork.constants.HttpMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Request<T> {
    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getHeadersString() {
        if (headers == null) return "";
        return headers.toString();
    }

    public HashMap<String, String> getVars() {
        return vars != null ? vars : new HashMap<String, String>();
    }

    public List<Pair<String, String>> getVarList() {
        return varList != null ? varList : new ArrayList<Pair<String, String>>();
    }

    public String getBody() {
        return body;
    }

    public String getBodyRaw() {
        return bodyRaw;
    }

    public String getBodyEncoded() {
        return bodyEncoded;
    }


    public String getMethod() {
        return method;
    }

    public String getHeader(Object key) {
        return headers.get(key);
    }

    public Set<String> getVarKeys() {
        return vars != null ? vars.keySet() : new HashSet<String>();
    }

    public boolean isFormEncoded() {
        return isFormEncoded;
    }

    private String url;
    private String method;
    private boolean isFormEncoded;
    private HashMap<String, String> headers;
    private HashMap<String, String> vars;
    private List<Pair<String, String>> varList;
    private String body;
    private String bodyRaw;
    private String bodyEncoded;

    protected Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.vars = builder.vars;
        this.varList = builder.varList;
        this.bodyRaw = builder.bodyRaw;
        this.bodyEncoded = builder.bodyEncoded;
        this.body = builder.body;
        this.isFormEncoded = builder.isFormEncoded;
    }


    public static class Builder {
        private String url;
        private boolean isFormEncoded;
        private String method;
        private HashMap<String, String> headers;
        private HashMap<String, String> vars;
        private List<Pair<String, String>> varList;
        private String body;
        private String bodyRaw;
        private String bodyEncoded;

        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder isFormEncoded(boolean isFormEncoded) {
            this.isFormEncoded = isFormEncoded;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder head() {
            this.method = HttpMethods.HEAD;
            return this;
        }

        public Builder get() {
            this.method = HttpMethods.GET;
            return this;
        }

        public Builder post() {
            this.method = HttpMethods.POST;
            return this;
        }

        public Builder delete() {
            this.method = HttpMethods.DELETE;
            return this;
        }

        public Builder put() {
            this.method = HttpMethods.PUT;
            return this;
        }

        public Builder vars(HashMap<String, String> vars) {
            this.vars = vars;
            return this;
        }

        public Builder varList(List<Pair<String, String>> varList) {
            this.varList = varList;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder bodyRaw(String bodyRaw) {
            this.bodyRaw = bodyRaw;
            return this;
        }

        public Builder bodyEncoded(String bodyEncoded) {
            this.bodyEncoded = bodyEncoded;
            return this;
        }

        //Test
        public Request build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new Request(this);
        }
    }
}
