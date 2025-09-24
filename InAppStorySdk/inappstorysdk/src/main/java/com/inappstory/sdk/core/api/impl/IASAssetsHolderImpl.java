package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IASAssetsHolderImpl implements IASAssetsHolder {
    private final IASCore core;


    private final ExecutorService downloader = Executors.newFixedThreadPool(5);
    private final List<SessionAsset> sessionAssets = new ArrayList<>();
    public IASAssetsHolderImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public List<SessionAsset> assets() {
        List<SessionAsset> assets = new ArrayList<>();
        synchronized (assetsLock) {
            assets.addAll(sessionAssets);
        }
        return assets;
    }

    @Override
    public void downloadAssets() {
        List<SessionAsset> assets = new ArrayList<>();
        synchronized (assetsLock) {
            assets.addAll(sessionAssets);
        }
        if (assets.isEmpty()) return;
        synchronized (assetsLock) {
            if (assetsIsDownloaded) return;
            if (assetsDownloadError) return;
            if (assetsIsLoading) return;
            assetsIsLoading = true;
        }
        List<Callable<Object>> assetTasks = new ArrayList<>(assets.size());

        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {

            }
        });
        final boolean[] assetsStatus = {true};

        for (final SessionAsset asset : assets) {
            assetTasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return new SessionAssetUseCase(core,
                            new UseCaseCallback<File>() {
                                @Override
                                public void onError(String message) {
                                    synchronized (assetsLock) {
                                        assetsStatus[0] = false;
                                    }
                                }

                                @Override
                                public void onSuccess(File result) {

                                }
                            },
                            asset
                    ).getFile();
                }
            });
        }
        try {
            downloader.invokeAll(assetTasks);
        } catch (InterruptedException e) {
            synchronized (assetsLock) {
                assetsStatus[0] = false;
            }
        }
        List<SessionAssetsIsReadyCallback> copyCallbacks = new ArrayList<>();
        synchronized (assetsLock) {
            assetsIsLoading = false;
            if (assetsStatus[0]) {
                assetsIsDownloaded = true;
            } else {
                assetsDownloadError = true;
            }
            copyCallbacks.addAll(callbacks);
            callbacks.clear();
        }
        for (SessionAssetsIsReadyCallback callback: copyCallbacks) {
            if (assetsStatus[0]) {
                callback.isReady();
            } else {
                callback.error();
            }
        }

    }

    @Override
    public void setAssets(List<SessionAsset> assets) {
        synchronized (assetsLock) {
            sessionAssets.clear();
            sessionAssets.addAll(assets);
        }
    }

    private final Object assetsLock = new Object();
    private boolean assetsIsDownloaded = false;
    private boolean assetsDownloadError = false;
    private boolean assetsIsLoading = false;

    @Override
    public boolean assetsIsDownloaded() {
        synchronized (assetsLock) {
            return assetsIsDownloaded;
        }
    }

    List<SessionAssetsIsReadyCallback> callbacks = new ArrayList<>();

    @Override
    public void addAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (assetsLock) {
            callbacks.add(callback);
        }
    }

    @Override
    public void checkOrAddAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (assetsLock) {
            if (assetsIsDownloaded) {
                callback.isReady();
            } else if (assetsDownloadError) {
                callback.error();
            } else {
                callbacks.add(callback);
            }
        }
    }

    @Override
    public void removeAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (assetsLock) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void clear() {
        synchronized (assetsLock) {
            callbacks.clear();
            assetsIsDownloaded = false;
            assetsIsLoading = false;
            assetsDownloadError = false;
        }
    }
}
