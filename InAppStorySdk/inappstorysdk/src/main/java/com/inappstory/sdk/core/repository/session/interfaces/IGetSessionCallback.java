package com.inappstory.sdk.core.repository.session.interfaces;

public interface IGetSessionCallback<T> {
    void onSuccess(T session);

    void onError();
}
