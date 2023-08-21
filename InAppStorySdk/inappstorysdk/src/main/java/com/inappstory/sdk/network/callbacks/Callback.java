package com.inappstory.sdk.network.callbacks;

import com.inappstory.sdk.network.models.Response;

import java.lang.reflect.Type;

public interface Callback<T> {
  void onSuccess(T response);
  void onFailure(Response response);
  Type getType();
}