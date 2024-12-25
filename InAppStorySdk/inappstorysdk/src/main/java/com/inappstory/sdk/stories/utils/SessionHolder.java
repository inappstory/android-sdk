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

    private final HashMap<String, SessionAsset> cacheObjects = new HashMap<>();
    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();

    private final Object cacheLock = new Object();

    private final HashSet<SessionAssetsIsReadyCallback> assetsIsReadyCallbacks = new HashSet<>();


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
    public void addSessionAssetsKeys(List<SessionAsset> cacheObjects) {
        synchronized (cacheLock) {
            this.cacheObjects.clear();
            this.allObjects.clear();
            for (SessionAsset object : cacheObjects) {
                this.cacheObjects.put(object.url, null);
                this.allObjects.put(object.url, object);
            }
        }
    }

    @Override
    public void addSessionAsset(SessionAsset object) {
        synchronized (cacheLock) {
            cacheObjects.put(object.url, object);
        }
    }

    @Override
    public void addSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (cacheLock) {
            assetsIsReadyCallbacks.add(callback);
        }
    }

    @Override
    public void removeSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (cacheLock) {
            assetsIsReadyCallbacks.remove(callback);
        }
    }

    @Override
    public boolean checkIfSessionAssetsIsReadySync() {
        synchronized (cacheLock) {
            return assetsIsReady;
        }
    }

    @Override
    public void assetsIsCleared() {
        this.assetsIsReady = false;
    }

    private boolean assetsIsReady = false;

    @Override
    public boolean checkIfSessionAssetsIsReadyAsync() {
        final boolean[] cachesIsReady = {true};
        synchronized (cacheLock) {
            for (Map.Entry<String, SessionAsset> entry : cacheObjects.entrySet()) {
                if (!cachesIsReady[0]) return false;
                if (entry.getValue() == null) return false;
                new SessionAssetLocalUseCase(
                        core,
                        new UseCaseCallback<File>() {
                            @Override
                            public void onError(String message) {
                                cachesIsReady[0] = false;
                            }

                            @Override
                            public void onSuccess(File result) {
                            }
                        },
                        entry.getValue()
                ).getFile();
            }

        }
        if (cachesIsReady[0]) {
            Set<SessionAssetsIsReadyCallback> temp = new HashSet<>();
            synchronized (cacheLock) {
                temp.addAll(assetsIsReadyCallbacks);
                assetsIsReadyCallbacks.clear();
            }
            for (SessionAssetsIsReadyCallback callback : temp) {
                callback.isReady();
            }
        }
        synchronized (cacheLock) {
            assetsIsReady = cachesIsReady[0];
        }
        return cachesIsReady[0];
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
