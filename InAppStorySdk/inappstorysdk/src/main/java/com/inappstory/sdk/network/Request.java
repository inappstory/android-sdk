package com.inappstory.sdk.network;

import android.os.AsyncTask;
import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


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

        public Builder delete() {
            this.method = NetworkHandler.DELETE;
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


    public Response execute() {
        Response s = null;
        String requestId = UUID.randomUUID().toString();
        try {
            s = NetworkHandler.doRequest(Request.this, requestId);
        } catch (SocketTimeoutException e) {
            s = new Response.Builder().code(-1).errorBody(e.getMessage()).build();
        } catch (SocketException e) {
            s = new Response.Builder().code(-2).errorBody(e.getMessage()).build();
        } catch (Exception e) {
            s = new Response.Builder().code(-3).errorBody(e.getMessage()).build();
        }
        s.logId = requestId;
        ApiLogResponse responseLog = new ApiLogResponse();
        responseLog.id = requestId;
        responseLog.timestamp = System.currentTimeMillis();
        responseLog.contentLength = s.contentLength;
        if (s.body != null) {
            responseLog.generateJsonResponse(s.code, s.body, s.headers);
        } else {
            responseLog.generateError(s.code, s.errorBody, s.headers);
        }
        InAppStoryManager.sendApiResponseLog(responseLog);
        return s;
    }

    public void enqueue(final Callback callback) {
        new AsyncTask<Void, String, Response>() {
            @Override
            protected Response doInBackground(Void... voids) {
                String requestId = UUID.randomUUID().toString();
                Response s = null;
                try {
                    s = NetworkHandler.doRequest(Request.this, requestId);
                } catch (SocketTimeoutException e) {
                    s = new Response.Builder().code(-1).errorBody(e.getMessage()).build();
                } catch (SocketException e) {
                    s = new Response.Builder().code(-2).errorBody(e.getMessage()).build();
                } catch (Exception e) {
                    s = new Response.Builder().code(-3).errorBody(e.getMessage()).build();
                }
                s.logId = requestId;
                return s;
            }

            @Override
            protected void onPostExecute(final Response result) {
                if (result != null) {
                    ApiLogResponse responseLog = new ApiLogResponse();
                    responseLog.id = result.logId;
                    responseLog.timestamp = System.currentTimeMillis();
                    responseLog.contentLength = result.contentLength;
                    if (result.body != null) {
                        responseLog.generateJsonResponse(result.code, result.body, result.headers);
                        InAppStoryManager.sendApiResponseLog(responseLog);
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
                    } else {
                        responseLog.generateJsonResponse(result.code, result.errorBody, result.headers);
                        InAppStoryManager.sendApiResponseLog(responseLog);
                        callback.onFailure(result);
                    }
                }
            }
        }.execute();
    }
}