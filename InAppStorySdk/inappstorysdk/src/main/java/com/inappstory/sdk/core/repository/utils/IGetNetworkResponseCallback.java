package com.inappstory.sdk.core.repository.utils;

import com.inappstory.sdk.core.repository.session.interfaces.NetworkErrorCallback;

public interface IGetNetworkResponseCallback<T>  extends NetworkErrorCallback {
    void onSuccess(T response);
}
