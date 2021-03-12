package com.inappstory.sdk.network;

import android.os.AsyncTask;

import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Set;


public final class Request<T> {
    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getVars() {
        return vars;
    }

    public String getBody() {
        return body;
    }


    public String getMethod() {
        return method;
    }

    public String getHeader(Object key) {
        return headers.get(key);
    }

    public Set<String> getVarKeys() {
        return vars.keySet();
    }

    private String url;
    private String method;

    public boolean isFormEncoded() {
        return isFormEncoded;
    }

    private boolean isFormEncoded;
    private HashMap<String, String> headers;
    private HashMap<String, String> vars;
    private String body;

    Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.vars = builder.vars;
        this.body = builder.body;
        this.isFormEncoded = builder.isFormEncoded;
    }



    public static class Builder {
        private String url;
        private boolean isFormEncoded;
        private String method;
        private HashMap<String, String> headers;
        private HashMap<String, String> vars;
        private String body;

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
            this.method = NetworkHandler.HEAD;
            return this;
        }

        public Builder get() {
            this.method = NetworkHandler.GET;
            return this;
        }

        public Builder post() {
            this.method = NetworkHandler.POST;
            return this;
        }

        public Builder put() {
            this.method = NetworkHandler.PUT;
            return this;
        }

        public Builder vars(HashMap<String, String> vars) {
            this.vars = vars;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        //Test
        public Request build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new Request(this);
        }
    }


    public Response execute() throws Exception {
        return NetworkHandler.doRequest(Request.this);
    }

    public void enqueue(final Callback callback) {
        new AsyncTask<Void, String, Response>() {
            @Override
            protected Response doInBackground(Void... voids) {
                Response s = null;
                try {
                    s = NetworkHandler.doRequest(Request.this);
                } catch (SocketTimeoutException e) {
                    s = new Response.Builder().code(-1).errorBody(e.getMessage()).build();
                } catch (SocketException e) {
                    s = new Response.Builder().code(-2).errorBody(e.getMessage()).build();
                } catch (Exception e) {
                    s = new Response.Builder().code(-3).errorBody(e.getMessage()).build();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final Response result) {
                if (result != null) {
                    if (result.body != null) {
                        //Gson gson = new Gson();
                        if (callback.getType() == null) {
                            callback.onSuccess(result);
                        } else {
                            if (callback.getType() instanceof ParameterizedType) {
                                ParameterizedType parameterizedType = (ParameterizedType) callback.getType();
                                Object obj = JsonParser.listFromJson(result.body,
                                        (Class)(parameterizedType.getActualTypeArguments()[0]));
                                callback.onSuccess(obj);
                            } else {
                                callback.onSuccess(JsonParser.fromJson(result.body, (Class)callback.getType()));
                            }
                        }
                    } else
                        callback.onFailure(result);
                }
            }
        }.execute();
    }
}