package com.inappstory.sdk.packages.core.base.network;


import com.inappstory.sdk.packages.core.base.network.callbacks.Callback;
import com.inappstory.sdk.packages.core.base.network.models.Request;
import com.inappstory.sdk.packages.core.base.network.models.Response;

public interface INetworkClient {
    void updateNetworkSettings(NetworkSettings settings);
    void enqueue(Request request, Callback callback);
    Response execute(Request request, Callback callback);
}
