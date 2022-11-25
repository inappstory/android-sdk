package com.inappstory.sdk.utils;

public interface ZipLoadCallback {
    void onLoad(String baseUrl, String filePath, String data);
    void onError();
    void onProgress(int loadedSize, int totalSize);
}
