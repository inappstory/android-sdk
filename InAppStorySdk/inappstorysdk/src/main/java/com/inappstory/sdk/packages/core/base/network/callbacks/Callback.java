package com.inappstory.sdk.packages.core.base.network.callbacks;

import com.inappstory.sdk.packages.core.base.network.models.Response;

import java.lang.reflect.Type;

public interface Callback<T> {
  void onSuccess(T response);
  void onEmptyContent();
  void onFailure(Response response);
  Type getType();
}