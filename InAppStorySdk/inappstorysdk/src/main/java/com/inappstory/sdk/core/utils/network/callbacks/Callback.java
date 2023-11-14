package com.inappstory.sdk.core.utils.network.callbacks;

import com.inappstory.sdk.core.utils.network.models.Response;

import java.lang.reflect.Type;

public interface Callback<T> {
  void onSuccess(T response);
  void onFailure(Response response);
  Type getType();
}