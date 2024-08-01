package com.inappstory.sdk.packages.core;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.sdk.IASLogger;
import com.inappstory.sdk.UserDebugLogManager;

public interface IIASCore {
    void setLogger(IASLogger logger);
    UserDebugLogManager getLogger();
    UtilModulesHolder getUtilModelsHolder();
}
