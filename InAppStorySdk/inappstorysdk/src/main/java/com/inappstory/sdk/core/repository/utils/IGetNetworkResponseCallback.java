package com.inappstory.sdk.core.repository.utils;

public interface IGetNetworkResponseCallback<T>  {
    void onSuccess(T response);

    void onError();
}
