package com.inappstory.sdk.packages.core;

import com.inappstory.sdk.IASLogger;
import com.inappstory.sdk.UserDebugLogManager;

public interface IIASCore {
    void setLogger(IASLogger logger);
    UserDebugLogManager getLogger();
}
