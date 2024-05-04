package com.inappstory.sdk.stories.cache.usecases;

public interface IGetStoryCoverCallback {
    void success(String file);

    void error();
}
