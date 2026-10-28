package com.inappstory.sdk.stories.utils;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.utils.ISessionHolder;

public class SessionHolder implements ISessionHolder {
    private final IASCore core;
    private CachedSessionData sessionData = null;

    public SessionHolder(IASCore core) {
        this.core = core;
    }

    private final Object sessionLock = new Object();


    @Override
    public boolean allowUGC() {
        synchronized (sessionLock) {
            return sessionData != null && sessionData.isAllowUGC;
        }
    }

    @Override
    public String getSessionId() {
        synchronized (sessionLock) {
            return sessionData != null ? sessionData.sessionId : "";
        }
    }

}