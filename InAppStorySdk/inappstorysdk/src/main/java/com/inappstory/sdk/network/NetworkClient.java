package com.inappstory.sdk.network;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.network.callbacks.Callback;
import com.inappstory.sdk.network.dummy.DummyApiInterface;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.models.Response;
import com.inappstory.sdk.network.utils.RequestSender;
import com.inappstory.sdk.network.utils.UserAgent;
import com.inappstory.sdk.network.utils.headers.Header;

import java.lang.reflect.ParameterizedType;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NetworkClient {
    private ApiInterface apiInterface;
    NetworkHandler networkHandler;

    private final String baseUrl;
    public String userAgent;

    public String getBaseUrl() {
        return baseUrl;
    }

    private final Context appContext;

    public NetworkClient(Context context, String baseUrl) {
        this.appContext = context.getApplicationContext();
        this.baseUrl = baseUrl;
        this.networkHandler = new NetworkHandler(baseUrl, this.appContext);
        this.userAgent = new UserAgent().generate(context);
    }
    public void clear() {
        apiInterface = null;
    }

    ExecutorService netExecutor = Executors.newFixedThreadPool(10);

    public void enqueue(final Request request, final Callback callback) {
        netExecutor.submit(new Callable<Response>() {
            @Override
            public Response call() {
                return execute(request, callback);
            }
        });
    }
    @WorkerThread
    public Response execute(Request request) {
        return execute(request, null);
    }


    public static final String NC_IS_UNAVAILABLE = "Network client is unavailable";
    @WorkerThread
    public Response execute(Request request, Callback callback) {
        Response response;
        String requestId = UUID.randomUUID().toString();
        try {
            response = new RequestSender().send(request, requestId);
            response.logId = requestId;
            if (callback == null) {
                return response;
            }
            if (response.body != null) {
                if (callback.getType() == null) {
                    callback.onSuccess(response);
                    return response;
                } else {
                    Object obj;
                    if (callback.getType() instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) callback.getType();
                        obj = JsonParser.listFromJson(response.body,
                                (Class) (parameterizedType.getActualTypeArguments()[0]));
                    } else {
                        obj = JsonParser.fromJson(response.body, (Class) callback.getType());
                    }
                    if (obj != null) {
                        callback.onSuccess(obj);
                        return response;
                    }
                }
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
            Context context,
            String[] exclude,
            List<Pair<String, String>> replace,
            boolean isFormEncoded,
            boolean hasBody
    ) {
        return networkHandler.generateHeaders(context, exclude, replace, isFormEncoded, hasBody);
    }
    public ApiInterface getApi() {
        if (getBaseUrl() == null) {
            if (ApiSettings.getInstance().getHost() == null) {
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
