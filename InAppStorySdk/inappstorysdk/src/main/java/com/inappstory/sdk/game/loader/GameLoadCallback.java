package com.inappstory.sdk.game.loader;

public interface GameLoadCallback {
    void onLoad(String baseUrl, String data);
    void onError();
    void onProgress(int loadedSize, int totalSize);
}
