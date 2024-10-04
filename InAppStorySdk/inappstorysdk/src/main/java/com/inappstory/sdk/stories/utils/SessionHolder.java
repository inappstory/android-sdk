package com.inappstory.sdk.stories.utils;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SessionHolder implements ISessionHolder {
    private Session session;
    private final IASCore core;

    public SessionHolder(IASCore core) {
        this.core = core;
    }

    private final Object sessionLock = new Object();

    private final HashMap<String, SessionAsset> cacheObjects = new HashMap<>();
    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();

    private final Object cacheLock = new Object();

    private HashSet<SessionAssetsIsReadyCallback> assetsIsReadyCallbacks = new HashSet<>();



    @Override
    public boolean allowUGC() {
        synchronized (sessionLock) {
            return session != null
                    && session.isAllowUgc;
        }
    }

    @Override
    public String getSessionId() {
        synchronized (sessionLock) {
            return session != null ? session.id : "";
        }
    }

    @Override
    public List<SessionAsset> getSessionAssets() {
        return new ArrayList<>(allObjects.values());
    }

    @Override
    public void setSession(Session session, boolean v1Disabled) {
        synchronized (sessionLock) {
            this.session = session;
            if (session != null && session.id != null) {
                core.statistic().createV1(session.id, v1Disabled);
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
                this.cacheObjects.put(object.filename, null);
                this.allObjects.put(object.filename, object);
            }
        }
    }

    @Override
    public void addSessionAsset(SessionAsset object) {
        synchronized (cacheLock) {
            cacheObjects.put(object.filename, object);
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
    public boolean checkIfSessionAssetsIsReady() {
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
    public boolean checkIfSessionAssetsIsReady(FilesDownloadManager filesDownloadManager) {
        final boolean[] cachesIsReady = {true};
        synchronized (cacheLock) {
            for (String key : cacheObjects.keySet()) {
                if (!cachesIsReady[0]) return false;
                SessionAsset asset = cacheObjects.get(key);
                if (asset == null) return false;
                new SessionAssetLocalUseCase(
                        filesDownloadManager,
                        new UseCaseCallback<File>() {
                            @Override
                            public void onError(String message) {
                                cachesIsReady[0] = false;
                            }

                            @Override
                            public void onSuccess(File result) {
                            }
                        },
                        asset
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
            if (session != null &&
                    oldSessionId != null &&
                    Objects.equals(session.id, oldSessionId)) {
                core.statistic().removeV1(oldSessionId);
                session = null;
            }
        }
    }
}
