package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASLayoutHolder;
import com.inappstory.sdk.core.network.content.models.LayoutResponse;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.CachedSessionData;
import com.inappstory.sdk.stories.cache.LayoutIsReadyCallback;

import java.lang.reflect.Type;
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
        mainLoaderThread.execute(new Runnable() {
            @Override
            public void run() {
                new ConnectionCheck().check(
                        core.appContext(),
                        new ConnectionCheckCallback(core) {
                            @Override
                            public void success() {
                                IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
                                CachedSessionData sessionData = settingsHolder.sessionData();
                                NetworkCallback<LayoutResponse> layoutCallback = new NetworkCallback<LayoutResponse>() {
                                    @Override
                                    public void onSuccess(LayoutResponse response) {
                                        IASLayoutHolderImpl.this.layout = response.layout;
                                        invokeIsReadyCallbacks();
                                    }

                                    @Override
                                    public Type getType() {
                                        return LayoutResponse.class;
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        invokeErrorCallbacks();
                                    }
                                };
                                core.network().enqueue(
                                        core.network().getApi().getLayout(
                                                sessionData.userId,
                                                sessionData.sessionId,
                                                sessionData.locale
                                        ),
                                        layoutCallback
                                );
                            }
                        }
                );
            }
        });
    }

    @Override
    public boolean layoutIsDownloaded() {
        synchronized (layoutDownloadLock) {
            return layout != null;
        }
    }

    @Override
    public void checkOrAddLayoutIsReadyCallback(LayoutIsReadyCallback callback) {
        String tempLayout;
        boolean isReady = false;
        synchronized (layoutDownloadLock) {
            if (layout != null) {
                isReady = true;
            } else {
                callbacks.add(callback);
                if (layoutIsLoading) {
                    callback.layoutIsLoading();
                    return;
                } else {
                    layoutIsLoading = true;
                }
            }
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
