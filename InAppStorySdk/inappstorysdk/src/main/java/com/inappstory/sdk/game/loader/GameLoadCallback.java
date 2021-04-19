package com.inappstory.sdk.game.loader;

import java.io.File;

public interface GameLoadCallback {
    void onLoad(File file);
    void onError();
    void onProgress(int loadedSize, int totalSize);
}
