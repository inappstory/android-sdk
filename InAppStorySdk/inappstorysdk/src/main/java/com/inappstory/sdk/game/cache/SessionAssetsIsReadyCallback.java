package com.inappstory.sdk.game.cache;

import java.util.List;

public interface SessionAssetsIsReadyCallback {
    void isReady();
    void assetsIsLoading();
    void error();
    List<String> usedAssets();
}
