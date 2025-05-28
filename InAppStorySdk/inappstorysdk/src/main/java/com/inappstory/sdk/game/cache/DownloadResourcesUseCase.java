package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.stories.cache.usecases.GameResourceUseCase;
import com.inappstory.sdk.utils.ProgressCallback;

import java.util.List;

public class DownloadResourcesUseCase {
    private final IASCore core;
    private final List<WebResource> resources;
    private long totalResourcesSize;
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
        for (WebResource resource : resources) {
            totalResourcesSize += resource.size;
        }
    }

    private boolean terminate;

    @WorkerThread
    public void download() {
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
                        public void onError(String message) {
                            useCaseCallback.onError(message);
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
