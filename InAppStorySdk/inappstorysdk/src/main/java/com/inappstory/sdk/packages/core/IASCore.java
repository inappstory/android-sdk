package com.inappstory.sdk.packages.core;

import com.inappstory.sdk.IASLogger;
import com.inappstory.sdk.UserDebugLogManager;

public class IASCore implements IIASCore {
    private static IIASCore INSTANCE;
    private static final Object lock = new Object();

    public static IIASCore getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new IASCore();
            return INSTANCE;
        }
    }

    @Override
    public void setLogger(IASLogger logger) {
        logManager.setLogger(logger);
    }

    @Override
    public UserDebugLogManager getLogger() {
        return logManager;
    }

    UserDebugLogManager logManager = new UserDebugLogManager();
}
