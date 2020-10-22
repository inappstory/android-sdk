package io.casestory.sdk.network;

import java.io.IOException;

public interface Call extends Cloneable {
  Response execute() throws IOException;
  void enqueue(Callback callback);
  boolean isExecuted();
  void cancel();
  boolean isCanceled();
  Call clone();
  Request request();
}