package com.inappstory.sdk.network;

import android.os.AsyncTask;
import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.api.models.logs.ApiLogResponse;

import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

    private String url;
    private String method;

    public boolean isFormEncoded() {
        return isFormEncoded;
    }

    private boolean isFormEncoded;
    private HashMap<String, String> headers;
    private HashMap<String, String> vars;
    private List<Pair<String, String>> varList;
    private String body;
    private String bodyRaw;
    private String bodyEncoded;

    Request(Builder builder) {
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
                        try {
                            if (callback.getType() == null) {
                                callback.onSuccess(result);
                            } else {
                                Object obj;
                                if (callback.getType() instanceof ParameterizedType) {
                                    ParameterizedType parameterizedType = (ParameterizedType) callback.getType();
                                    obj = JsonParser.listFromJson(result.body,
                                            (Class) (parameterizedType.getActualTypeArguments()[0]));
                                } else {
                                    obj = JsonParser.fromJson(result.body, (Class) callback.getType());
                                }
                                if (obj != null) {
                                    callback.onSuccess(obj);
                                } else {
                                    sendError(responseLog, result, result.errorBody, callback);
                                }
                            }
                        } catch (Exception e) {
                            sendError(responseLog, result, e.getMessage(), callback);
                        }
                    } else {
                        sendError(responseLog, result, result.errorBody, callback);
                    }
                }
            }
        }.execute();
    }

    private void sendError(ApiLogResponse responseLog, Response result, String error, Callback callback) {
        responseLog.generateJsonResponse(result.code, error, result.headers);
        InAppStoryManager.sendApiResponseLog(responseLog);
        callback.onFailure(result);
    }
}