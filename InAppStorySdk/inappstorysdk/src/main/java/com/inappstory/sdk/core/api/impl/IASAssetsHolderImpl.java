package com.inappstory.sdk.core.api.impl;

import android.util.Log;
import android.util.Pair;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.game.cache.UseCaseError;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IASAssetsHolderImpl implements IASAssetsHolder {
    private final IASCore core;


    private ExecutorService downloader = Executors.newFixedThreadPool(5);
    private final ExecutorService mainLoaderThread = Executors.newSingleThreadExecutor();
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

    private void loadAssets() {
        List<SessionAsset> assets = new ArrayList<>();
        synchronized (assetsLock) {
            assets.addAll(sessionAssets);
            if (assets.isEmpty()) return;
            if (assetsIsDownloaded) return;
            if (assetsDownloadError) return;
            if (assetsIsLoading) return;
            assetsIsLoading = true;
        }
        final boolean[] assetsStatus = {true};
        Collection<Future<?>> futures = new ArrayList<>();
        for (final SessionAsset asset : assets) {
            futures.add(downloader.submit(new Runnable() {
                @Override
                public void run() {
                    Log.e("LoadContentPage", "asset download start " + asset.url);
                    new SessionAssetUseCase(core,
                            new UseCaseCallback<Pair<SessionAsset, File>>() {
                                @Override
                                public void onError(UseCaseError error) {
                                    synchronized (assetsLock) {
                                        assetsStatus[0] = false;
                                    }
                                }

                                @Override
                                public void onSuccess(Pair<SessionAsset, File> result) {
                                    String url = result.first.url;
                                    synchronized (assetsLock) {
                                        if (!cachedAssetUrls.contains(url)) {
                                            cachedAssetUrls.add(url);
                                        }
                                    }
                                }
                            },
                            asset
                    ).getFile();
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                boolean success = false;
                synchronized (assetsLock) {
                    success = assetsStatus[0];
                }
                if (!success) {
                    downloader.shutdownNow();
                    downloader = Executors.newFixedThreadPool(5);
                    break;
                }
                future.get();

            } catch (InterruptedException | ExecutionException e) {
                synchronized (assetsLock) {
                    assetsStatus[0] = false;
                }
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
        for (SessionAssetsIsReadyCallback callback : copyCallbacks) {
            if (assetsStatus[0]) {
                callback.isReady();
            } else {
                callback.error();
            }
        }

    }

    @Override
    public void downloadAssets() {
        mainLoaderThread.execute(new Runnable() {
            @Override
            public void run() {
                loadAssets();
            }
        });

    }

    @Override
    public void reloadAssets(SessionAssetsIsReadyCallback callback) {
        synchronized (assetsLock) {
            if (assetsIsDownloaded) {
                callback.isReady();
                return;
            }
            if (assetsDownloadError) {
                assetsDownloadError = false;
            }
            callbacks.add(callback);
            if (assetsIsLoading) {
                return;
            }
        }
        callback.assetsIsLoading();
        downloadAssets();
    }

    @Override
    public void setAssets(List<SessionAsset> assets) {
        List<SessionAsset> orderedAssets = new ArrayList<>();
        synchronized (assetsLock) {
            layoutAssetUrls.clear();
            for (SessionAsset sessionAsset : assets) {
                if (sessionAsset.isMainAsset()) {
                    layoutAssetUrls.add(sessionAsset.url);
                    orderedAssets.add(0, sessionAsset);
                } else {
                    orderedAssets.add(sessionAsset);
                }
            }
            sessionAssets.clear();
            sessionAssets.addAll(orderedAssets);
        }
    }

    private final Object assetsLock = new Object();
    private boolean assetsIsDownloaded = false;
    private final List<String> cachedAssetUrls = new ArrayList<>();
    private final Set<String> layoutAssetUrls = new HashSet<>();
    private boolean assetsDownloadError = false;
    private boolean assetsIsLoading = false;

    @Override
    public boolean assetsIsDownloaded(Set<String> assetUrls) {
        synchronized (assetsLock) {
            if (assetsIsDownloaded) {
                return true;
            }
            if (assetUrls == null) {
                return false;
            }
            Set<String> allCheckUrls = new HashSet<>(layoutAssetUrls);
            allCheckUrls.addAll(assetUrls);
            for (String assetUrl : allCheckUrls) {
                if (!cachedAssetUrls.contains(assetUrl))
                    return false;
            }
            return true;
        }
    }

    List<SessionAssetsIsReadyCallback> callbacks = new ArrayList<>();

    @Override
    public void addAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        synchronized (assetsLock) {
            if (assetsIsLoading) callback.assetsIsLoading();
            callbacks.add(callback);
        }
    }

    @Override
    public void checkOrAddAssetsIsReadyCallback(SessionAssetsIsReadyCallback callback) {
        if (assetsIsDownloaded(callback.usedAssets())) {
            callback.isReady();
            return;
        }
        synchronized (assetsLock) {
            if (assetsDownloadError) {
                callback.error();
            } else {
                if (assetsIsLoading) {
                    callback.assetsIsLoading();
                }
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
            cachedAssetUrls.clear();
            assetsIsDownloaded = false;
            assetsIsLoading = false;
            assetsDownloadError = false;
        }
    }
}
