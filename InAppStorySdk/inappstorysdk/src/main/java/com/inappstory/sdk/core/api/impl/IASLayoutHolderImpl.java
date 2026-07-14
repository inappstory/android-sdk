package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASLayoutHolder;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseError;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.cache.LayoutIsReadyCallback;
import com.inappstory.sdk.stories.cache.usecases.LayoutUseCase;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IASLayoutHolderImpl implements IASLayoutHolder {
    public IASLayoutHolderImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public String layout() {
        synchronized (layoutDownloadLock) {
            return layout;
        }
    }

    private final IASCore core;

    private final Object layoutDownloadLock = new Object();
    private final ExecutorService mainLoaderThread = Executors.newSingleThreadExecutor();
    private boolean layoutIsLoading = false;
    private String layout;


    Set<LayoutIsReadyCallback> callbacks = new HashSet<>();

    private void invokeIsReadyCallbacks() {
        Set<LayoutIsReadyCallback> tempCallbacks = new HashSet<>();
        synchronized (layoutDownloadLock) {
            tempCallbacks.addAll(callbacks);
        }
        for (LayoutIsReadyCallback callback : tempCallbacks) {
            callback.isReady();
        }
    }

    private void invokeErrorCallbacks() {
        Set<LayoutIsReadyCallback> tempCallbacks = new HashSet<>();
        synchronized (layoutDownloadLock) {
            tempCallbacks.addAll(callbacks);
        }
        for (LayoutIsReadyCallback callback : tempCallbacks) {
            callback.error();
        }
    }

    private void downloadLayout() {

        final CachedSessionData cachedSessionData = ((IASDataSettingsHolder) core.settingsAPI()).sessionData();
        if (cachedSessionData != null) {
            mainLoaderThread.execute(new Runnable() {
                @Override
                public void run() {
                    new LayoutUseCase(
                            core,
                            new UseCaseCallback<String>() {
                                @Override
                                public void onError(UseCaseError error) {
                                    synchronized (layoutDownloadLock) {
                                        layoutIsLoading = false;
                                    }
                                    invokeErrorCallbacks();
                                }

                                @Override
                                public void onSuccess(String result) {
                                    synchronized (layoutDownloadLock) {
                                        layoutIsLoading = false;
                                        IASLayoutHolderImpl.this.layout = result;
                                    }
                                    invokeIsReadyCallbacks();
                                }
                            },
                            cachedSessionData.layoutTimestamp
                    ).getFile();
                }
            });
        } else {
            invokeErrorCallbacks();
        }
    }

    @Override
    public boolean layoutIsDownloaded() {
        synchronized (layoutDownloadLock) {
            return layout != null;
        }
    }

    @Override
    public void loadLocalLayout() {
        boolean isReady = false;
        boolean isLoading = false;
        synchronized (layoutDownloadLock) {
            if (layout != null) {
                isReady = true;
            } else {
                if (layoutIsLoading) {
                    isLoading = true;
                }
            }
        }
        if (!(isLoading || isReady)) {
            final CachedSessionData cachedSessionData = ((IASDataSettingsHolder) core.settingsAPI()).sessionData();
            if (cachedSessionData != null)
                mainLoaderThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        new LayoutUseCase(
                                core,
                                new UseCaseCallback<String>() {
                                    @Override
                                    public void onError(UseCaseError error) {

                                    }

                                    @Override
                                    public void onSuccess(String result) {
                                        synchronized (layoutDownloadLock) {
                                            IASLayoutHolderImpl.this.layout = result;
                                        }
                                    }
                                },
                                cachedSessionData.layoutTimestamp
                        ).getLocalFile();
                    }
                });
        }

    }

    @Override
    public void checkOrAddLayoutIsReadyCallback(LayoutIsReadyCallback callback) {
        boolean isReady = false;
        boolean isLoading = false;
        synchronized (layoutDownloadLock) {
            if (layout != null) {
                isReady = true;
            } else {
                callbacks.add(callback);
                if (layoutIsLoading) {
                    isLoading = true;
                } else {
                    layoutIsLoading = true;
                }
            }
        }
        if (isLoading) {
            callback.layoutIsLoading();
            return;
        }
        if (isReady) {
            callback.isReady();
        } else {
            downloadLayout();
        }
    }

    @Override
    public void removeLayoutIsReadyCallback(LayoutIsReadyCallback callback) {
        synchronized (layoutDownloadLock) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void clear() {
        synchronized (layoutDownloadLock) {
            layout = null;
        }
    }
}
