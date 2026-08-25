package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.usecases.InGameResourceUseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InGameResourceDownloadManager {
    private final Map<String, InGameResourceCallbacks> callbacksByUrl = new HashMap<>();
    private final Object callbacksLock = new Object();
    private final IASCore core;
    private final Set<String> launchedUrls = new HashSet<>();
    private final ExecutorService downloadExecutor = Executors.newSingleThreadExecutor();

    public InGameResourceDownloadManager(IASCore core) {
        this.core = core;
    }

    private class InGameResourceCallbacks {
        List<InGameResourceDownloadCallback> callbacks = new ArrayList<>();
    }

    public void addNewTask(
            String zipUrl,
            String gameInstanceId,
            final WebResource webResource,
            InGameResourceDownloadCallback callback
    ) {
        String key = webResource.uniqueKey();
        synchronized (callbacksLock) {
            InGameResourceCallbacks inGameResourceCallbacks;
            inGameResourceCallbacks = callbacksByUrl.get(key);
            if (inGameResourceCallbacks == null) {
                inGameResourceCallbacks = new InGameResourceCallbacks();
                callbacksByUrl.put(key, inGameResourceCallbacks);
            }
            inGameResourceCallbacks.callbacks.add(callback);
            if (launchedUrls.contains(key)) return;
            launchedUrls.add(key);
        }
        downloadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                new InGameResourceUseCase(
                        core,
                        zipUrl,
                        gameInstanceId,
                        new UseCaseCallback<Void>() {
                            @Override
                            public void onError(UseCaseError error) {
                                urlDownloadError(webResource, error);
                            }

                            @Override
                            public void onSuccess(Void result) {
                                urlDownloadSuccessful(webResource);
                            }
                        },
                        webResource
                );
            }
        });

    }

    private void urlDownloadSuccessful(WebResource webResource) {
        String key = webResource.uniqueKey();
        List<InGameResourceDownloadCallback> callbacks;
        synchronized (callbacksLock) {
            launchedUrls.remove(key);
            InGameResourceCallbacks inGameResourceCallbacks;
            inGameResourceCallbacks = callbacksByUrl.remove(key);
            if (inGameResourceCallbacks == null) return;
            callbacks = inGameResourceCallbacks.callbacks;
        }
        for (InGameResourceDownloadCallback callback : callbacks) {
            callback.invoke(new InGameResourceDownloadSuccess());
        }
    }

    private void urlDownloadError(WebResource webResource, UseCaseError error) {
        String key = webResource.uniqueKey();
        List<InGameResourceDownloadCallback> callbacks;
        synchronized (callbacksLock) {
            launchedUrls.remove(key);
            InGameResourceCallbacks inGameResourceCallbacks;
            inGameResourceCallbacks = callbacksByUrl.remove(key);
            if (inGameResourceCallbacks == null) return;
            callbacks = inGameResourceCallbacks.callbacks;
        }
        for (InGameResourceDownloadCallback callback : callbacks) {
            callback.invoke(new InGameResourceDownloadError(error.message()));
        }
    }
}
