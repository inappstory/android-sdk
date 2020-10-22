package io.casestory.sdk.network;

import java.lang.reflect.Type;

public interface Callback<T> {
  void onSuccess(T response);
  void onFailure(Response response);
  Type getType();
}