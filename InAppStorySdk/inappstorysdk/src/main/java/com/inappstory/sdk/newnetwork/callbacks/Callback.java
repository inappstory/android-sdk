package com.inappstory.sdk.newnetwork.callbacks;

import com.inappstory.sdk.newnetwork.models.Response;

import java.lang.reflect.Type;

public interface Callback<T> {
  void onSuccess(T response);
  void onFailure(Response response);
  Type getType();
}