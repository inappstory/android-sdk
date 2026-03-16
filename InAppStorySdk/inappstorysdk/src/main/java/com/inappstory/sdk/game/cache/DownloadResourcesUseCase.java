package com.inappstory.sdk.game.cache;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.GameResourceUseCase;
import com.inappstory.sdk.stories.cache.usecases.SessionAssetUseCase;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadResourcesUseCase {
    private final IASCore core;
    private final List<WebResource> resources;
    private long totalResourcesSize;
    private long totalLoadedResourcesSize;
    private final Object loadProgressLock = new Object();
    private final Object filesLock = new Object();
    private final String gameInstanceId;
    private final String zipUrl;
    private final DownloadInterruption interruption;
    private final ProgressCallback progressCallback;
    private final UseCaseCallback<Void> useCaseCallback;

    public DownloadResourcesUseCase(
            IASCore core,
            List<WebResource> resources,
            String gameInstanceId,
            String zipUrl,
            DownloadInterruption interruption,
            ProgressCallback progressCallback,
            UseCaseCallback<Void> useCaseCallback
    ) {
        this.resources = resources;
        this.core = core;
        this.progressCallback = progressCallback;
        this.useCaseCallback = useCaseCallback;
        this.gameInstanceId = gameInstanceId;
        this.interruption = interruption;
        this.zipUrl = zipUrl;
        if (resources == null) return;
        totalResourcesSize = 0;
        totalLoadedResourcesSize = 0;
        for (WebResource resource : resources) {
            totalResourcesSize += resource.size;
        }
    }

    private ExecutorService loadResourcesThreads = Executors.newFixedThreadPool(5);
    private boolean terminate;

    private void updateLoadProgress(long newPart) {
        synchronized (loadProgressLock) {
            totalLoadedResourcesSize += newPart;
            if (totalLoadedResourcesSize > totalResourcesSize)
                totalLoadedResourcesSize = totalResourcesSize;
            progressCallback.onProgress(
                    totalLoadedResourcesSize,
                    totalResourcesSize
            );
        }
    }

    @WorkerThread
    private void downloadParallel() {
        final boolean[] assetsStatus = {true};
        Collection<Future<?>> futures = new ArrayList<>();
        for (final WebResource resource : resources) {
            futures.add(loadResourcesThreads.submit(new Runnable() {
                @Override
                public void run() {
                    final long[] lastLoadedSize = new long[1];
                    new GameResourceUseCase(
                            core,
                            zipUrl,
                            gameInstanceId,
                            new ProgressCallback() {
                                @Override
                                public void onProgress(long loadedSize, long totalSize) {
                                    long newPart = loadedSize - lastLoadedSize[0];
                                    lastLoadedSize[0] = loadedSize;
                                    updateLoadProgress(newPart);
                                }
                            },
                            interruption,
                            new UseCaseCallback<Void>() {
                                @Override
                                public void onError(UseCaseError error) {
                                    useCaseCallback.onError(error);
                                    terminate = true;
                                    synchronized (filesLock) {
                                        assetsStatus[0] = false;
                                    }
                                }

                                @Override
                                public void onSuccess(Void result) {

                                }
                            },
                            resource
                    ).getFile();
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                boolean success = false;
                synchronized (filesLock) {
                    success = assetsStatus[0];
                }
                if (!success) {
                    loadResourcesThreads.shutdownNow();
                    loadResourcesThreads = Executors.newFixedThreadPool(5);
                    break;
                }
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                synchronized (filesLock) {
                    assetsStatus[0] = false;
                }
            }
        }
        synchronized (filesLock) {
            if (assetsStatus[0]) {
                useCaseCallback.onSuccess(null);
            }
        }
    }

    @WorkerThread
    public void download() {
        downloadParallel();
    }

    @WorkerThread
    private void downloadSync() {
        final long[] cnt = new long[1];
        terminate = false;
        for (WebResource resource : resources) {
            if (terminate) {
                return;
            }
            new GameResourceUseCase(
                    core,
                    zipUrl,
                    gameInstanceId,
                    new ProgressCallback() {
                        @Override
                        public void onProgress(long loadedSize, long totalSize) {
                            progressCallback.onProgress(
                                    cnt[0] + loadedSize,
                                    totalResourcesSize
                            );
                        }
                    },
                    interruption,
                    new UseCaseCallback<Void>() {
                        @Override
                        public void onError(UseCaseError error) {
                            useCaseCallback.onError(error);
                            terminate = true;
                        }

                        @Override
                        public void onSuccess(Void result) {

                        }
                    },
                    resource
            ).getFile();
            cnt[0] = cnt[0] + resource.size;
        }
        useCaseCallback.onSuccess(null);
    }
}
