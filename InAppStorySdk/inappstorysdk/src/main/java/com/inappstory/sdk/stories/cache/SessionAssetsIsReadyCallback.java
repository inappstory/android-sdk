package com.inappstory.sdk.stories.cache;

import java.util.Set;

public interface SessionAssetsIsReadyCallback {
    void isReady();
    void assetsIsLoading();
    void error();
    Set<String> usedAssets();
}
