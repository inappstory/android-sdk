package com.inappstory.sdk.stories.utils;


import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.core.network.content.models.Session;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SessionHolder implements ISessionHolder {
    private final IASCore core;
    private CachedSessionData sessionData = null;

    public SessionHolder(IASCore core) {
        this.core = core;
    }

    private final Object sessionLock = new Object();

    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();


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

    @Override
    public List<SessionAsset> getSessionAssets() {
        return new ArrayList<>(allObjects.values());
    }

    @Override
    public void setSession(CachedSessionData sessionData, boolean v1Disabled) {
        synchronized (sessionLock) {
            this.sessionData = sessionData;
            if (sessionData != null && sessionData.sessionId != null) {
                core.statistic().createV1(sessionData.sessionId, v1Disabled);
            }
            core.statistic().clearViewedIds();
        }
    }

    @Override
    public void clear(String oldSessionId) {
        synchronized (sessionLock) {
            core.statistic().clearViewedIds();
            if (sessionData != null &&
                    oldSessionId != null &&
                    Objects.equals(sessionData.sessionId, oldSessionId)) {
                core.statistic().removeV1(oldSessionId);
                sessionData = null;
            }
        }
    }

    @Override
    public CachedSessionData sessionData() {
        synchronized (sessionLock) {
            return sessionData;
        }
    }
}
