package com.inappstory.sdk.utils;

public interface ZipLoadCallback extends ProgressCallback {
    void onLoad(String baseUrl, String data);
    void onError(String error);
}
