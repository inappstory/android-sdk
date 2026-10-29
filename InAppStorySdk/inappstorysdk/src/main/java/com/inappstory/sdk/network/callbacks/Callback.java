package com.inappstory.sdk.network.callbacks;

import com.inappstory.sdk.network.models.Response;

public interface Callback<T> {
  void onSuccess(T response);
  void onRawResponse(Response response);
  void onEmptyContent();
  void onFailure(Response response);
}