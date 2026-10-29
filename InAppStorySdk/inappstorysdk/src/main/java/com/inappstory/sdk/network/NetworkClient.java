package com.inappstory.sdk.network;

import android.util.Pair;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.network.callbacks.Callback;
import com.inappstory.sdk.network.dummy.DummyApiInterface;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.utils.RequestSender;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.network.utils.headers.Header;
import com.inappstory.sdk.parse.to.ToModelParser;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NetworkClient {
    private ApiInterface apiInterface;
    private NetworkHandler networkHandler;

    private String baseUrl;

    public String userAgent() {
        return new UserAgent().generate(core);
    }

    private final IASCore core;

    public String getBaseUrl() {
        return baseUrl;
    }


    public NetworkClient(IASCore core) {
        this.core = core;
    }

    public void clear() {
        apiInterface = null;
    }

    public void setBaseUrl(String baseUrl) {
        if (this.baseUrl != null && Objects.equals(this.baseUrl, baseUrl)) return;
        this.apiInterface = null;
        this.baseUrl = baseUrl;
        this.networkHandler = new NetworkHandler(baseUrl, core);
    }

    ExecutorService netExecutor = Executors.newFixedThreadPool(10);

    public static final String NC_IS_UNAVAILABLE = "Network client is unavailable";


    public <T, M extends ToModelParser<T>> void enqueue(
            final Request request,
            final Callback<T> callback,
            final Class<M> parserClass
    ) {
        enqueue(request, callback, parserClass, null);
    }

    public <T> void enqueue(final Request request,
                            final Callback<T> callback
    ) {
        enqueue(request, callback, null, null);
    }

    public <T> void enqueue(final Request request
    ) {
        enqueue(request, null, null, null);
    }

    @WorkerThread
    public Response execute(Request request) {
        if (networkHandler == null) {
            return new Response
                    .Builder().code(-4)
                    .errorBody("InAppStoryManager wasn't initialized")
                    .build();
        }
        return execute(request, null, null, null);
    }

    public <T, M extends ToModelParser<T>> void enqueue(
            final Request request,
            final Callback<T> callback,
            Class<M> parserClass,
            final RequestLocalParameters requestLocalParameters
    ) {
        if (networkHandler == null) {
            callback.onFailure(
                    new Response
                            .Builder().code(-4)
                            .errorBody("InAppStoryManager wasn't initialized")
                            .build()
            );
            return;
        }
        netExecutor.submit(new Callable<Response>() {
            @Override
            public Response call() {
                return execute(request, callback, parserClass, requestLocalParameters);
            }
        });
    }

    public <T, M extends ToModelParser<T>> Response execute(
            Request request,
            Callback<T> callback,
            Class<M> parserClass,
            RequestLocalParameters requestLocalParameters
    ) {
        Response response;
        String requestId = UUID.randomUUID().toString();
        try {
            response = new RequestSender().send(request, requestId);

            response.logId = requestId;
            if (callback == null) {
                return response;
            }
            IASDataSettingsHolder dataSettingsHolder = (IASDataSettingsHolder) core.settingsAPI();
            RequestLocalParameters currentParameters = new RequestLocalParameters()
                    .sessionId(dataSettingsHolder.sessionIdOrEmpty())
                    .userId(dataSettingsHolder.userId())
                    .sendStatistic(dataSettingsHolder.sendStatistic())
                    .anonymous(dataSettingsHolder.anonymous())
                    .locale(dataSettingsHolder.lang());
            if (requestLocalParameters != null && !requestLocalParameters.equals(currentParameters)) {
                response = new Response.Builder().code(-5).errorBody("User id or locale was changed").build();
                response.logId = requestId;
                callback.onFailure(response);
                return response;
            }
            if (response.code == 204) {
                callback.onEmptyContent();
                return response;
            } else if (response.body != null) {
                if (parserClass == null) {
                    callback.onSuccess(null);
                    callback.onRawResponse(response);
                } else {
                    M parser = parserClass.newInstance();
                    T obj = parser.parse();
                    if (obj != null) {
                        callback.onSuccess(obj);
                    } else {
                        response = new Response.Builder().code(response.code)
                                .errorBody(
                                        parser.parseError()
                                ).build();
                        response.logId = requestId;
                        callback.onFailure(response);
                    }
                }
                return response;
            }
            response = new Response.Builder().code(response.code)
                    .errorBody(response.errorBody).build();
            response.logId = requestId;
            callback.onFailure(response);
        } catch (SocketTimeoutException e) {
            response = new Response.Builder().code(-1).errorBody(e.getMessage()).build();
            response.logId = requestId;
            if (callback != null) {
                callback.onFailure(response);
            }
        } catch (SocketException e) {
            response = new Response.Builder().code(-2).errorBody(e.getMessage()).build();
            response.logId = requestId;
            if (callback != null) {
                callback.onFailure(response);
            }
        } catch (Exception e) {
            response = new Response.Builder().code(-4).errorBody(e.getMessage()).build();
            response.logId = requestId;
            if (callback != null) {
                callback.onFailure(response);
            }
        }
        return response;
    }


    public List<Header> generateHeaders(
            String[] exclude,
            List<Pair<String, String>> replace,
            boolean isFormEncoded,
            boolean hasBody
    ) {
        return networkHandler.generateHeaders(exclude, replace, isFormEncoded, hasBody);
    }

    public ApiInterface getApi() {
        if (getBaseUrl() == null) {
            if (core.projectSettingsAPI().host() == null) {
                return new DummyApiInterface();
            }
            apiInterface = null;
        }
        if (apiInterface == null) {
            apiInterface = networkHandler.implement(ApiInterface.class);
        }
        return apiInterface;
    }
}
