package com.inappstory.sdk.stories.utils;


import android.util.Log;

import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.stories.api.models.Session;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.api.models.StatisticPermissions;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetLocalUseCase;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
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
    private Session session;

    private final Object sessionLock = new Object();

    private final HashMap<String, OldStatisticManager> statisticManagers = new HashMap<>();

    private final HashMap<String, SessionAsset> cacheObjects = new HashMap<>();
    private final HashMap<String, SessionAsset> allObjects = new HashMap<>();

    private final Object cacheLock = new Object();
    private final Object sessionAssetIsReadyLock = new Object();

    private HashSet<SessionAssetsIsReadyCallback> assetsIsReadyCallbacks = new HashSet<>();


    private final ArrayList<Integer> viewed = new ArrayList<>();

    @Override
    public boolean allowStatV1() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowStatV1;
        }
    }

    @Override
    public boolean allowStatV2() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowStatV2;
        }
    }

    @Override
    public boolean allowProfiling() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowProfiling;
        }
    }

    @Override
    public boolean allowCrash() {
        synchronized (sessionLock) {
            return session != null
                    && session.statisticPermissions != null
                    && session.statisticPermissions.allowCrash;
        }
    }

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
        synchronized (cacheLock) {
            return new ArrayList<>(allObjects.values());
        }
    }

    @Override
    public void setSessionPermissions(StatisticPermissions statisticPermissions) {
        synchronized (sessionLock) {
            if (session != null) session.statisticPermissions = statisticPermissions;
        }
    }

    @Override
    public void setSession(Session session) {
        synchronized (sessionLock) {
            this.session = session;
            if (session != null && session.id != null) {
                statisticManagers.put(session.id, new OldStatisticManager());
            }
            viewed.clear();
        }
    }

    @Override
    public void addViewedId(int id) {
        synchronized (sessionLock) {
            viewed.add(id);
        }
    }

    @Override
    public boolean hasViewedId(int id) {
        synchronized (sessionLock) {
            return viewed.contains(id);
        }
    }

    @Override
    public boolean hasViewedIds() {
        synchronized (sessionLock) {
            return viewed.size() > 0;
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
        synchronized (sessionAssetIsReadyLock) {
            assetsIsReadyCallbacks.add(callback);
        }
    }

    @Override
    public void removeSessionAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (sessionAssetIsReadyLock) {
            assetsIsReadyCallbacks.remove(callback);
        }
    }

    @Override
    public boolean checkIfSessionAssetsIsReady() {
        synchronized (sessionAssetIsReadyLock) {
            return assetsIsReady;
        }
    }

    @Override
    public void assetsIsCleared() {
        synchronized (sessionAssetIsReadyLock) {
            this.assetsIsReady = false;
        }
    }

    private boolean assetsIsReady = false;

    @Override
    public void checkIfSessionAssetsIsReady(
            SessionAsset sessionAsset,
            FilesDownloadManager filesDownloadManager
    ) {
        Map<String, SessionAsset> localCacheObjects = new HashMap<>();
        synchronized (cacheLock) {
            localCacheObjects.putAll(cacheObjects);
        }
        for (String key : localCacheObjects.keySet()) {
            SessionAsset asset = localCacheObjects.get(key);
            if (asset == null) {
               /* Log.e("SessionAssetsIsReady", sessionAsset.url + " " + sessionAsset.filename +
                        "\n" + key);*/
                return;
            }
        }
       // Log.e("SessionAssetsIsReady", sessionAsset.url + " " + sessionAsset.filename + " complete");
        List<SessionAsset> assets = new ArrayList<>(localCacheObjects.values());
        checkLocalAsset(
                filesDownloadManager,
                assets,
                0,
                new Runnable() {
                    @Override
                    public void run() {
                        Set<SessionAssetsIsReadyCallback> temp = new HashSet<>();
                        synchronized (sessionAssetIsReadyLock) {
                            assetsIsReady = true;
                            temp.addAll(assetsIsReadyCallbacks);
                            assetsIsReadyCallbacks.clear();
                        }
                        for (SessionAssetsIsReadyCallback callback : temp) {
                            callback.isReady();
                        }

                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (sessionAssetIsReadyLock) {
                            assetsIsReady = false;
                        }
                    }
                }
        );
    }

    private void checkLocalAsset(
            final FilesDownloadManager filesDownloadManager,
            final List<SessionAsset> assets,
            final int index,
            final Runnable check,
            final Runnable error
    ) {
        if (index >= assets.size()) {
            check.run();
        } else {
            new SessionAssetLocalUseCase(
                    filesDownloadManager,
                    new UseCaseCallback<File>() {
                        @Override
                        public void onError(String message) {
                            error.run();
                        }

                        @Override
                        public void onSuccess(File result) {
                            checkLocalAsset(
                                    filesDownloadManager,
                                    assets,
                                    index + 1,
                                    check,
                                    error
                            );
                        }
                    },
                    assets.get(index)
            ).getFile();
        }
    }

    @Override
    public OldStatisticManager currentStatisticManager() {
        synchronized (sessionLock) {
            if (session == null || session.id == null) return null;
            return statisticManagers.get(session.id);
        }
    }

    @Override
    public OldStatisticManager getStatisticManager(String sessionId) {
        synchronized (sessionLock) {
            return statisticManagers.get(sessionId);
        }
    }

    @Override
    public void clear(String oldSessionId) {
        synchronized (sessionLock) {
            viewed.clear();
            if (session != null &&
                    oldSessionId != null &&
                    Objects.equals(session.id, oldSessionId)) {
                statisticManagers.remove(oldSessionId);
                session = null;
            }
        }
    }
}
