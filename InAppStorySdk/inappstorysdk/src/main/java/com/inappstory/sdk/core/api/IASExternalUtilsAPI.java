package com.inappstory.sdk.core.api;

import com.inappstory.iasutilsconnector.UtilModulesHolder;

public interface IASExternalUtilsAPI {
    UtilModulesHolder getUtilsAPI();
    boolean hasLottieAnimation();
    void init();
}
