package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.utils.ProgressCallback;

import java.util.List;

public class DownloadResourcesUseCase {
    private List<WebResource> resources;
    private long totalResourcesSize;
    private String gameInstanceId;
    private final DownloadInterruption interruption;

    public DownloadResourcesUseCase(
            List<WebResource> resources,
            String gameInstanceId,
            DownloadInterruption interruption
    ) {
        this.resources = resources;
        this.gameInstanceId = gameInstanceId;
        this.interruption = interruption;
        if (resources == null) return;
        totalResourcesSize = 0;
        for (WebResource resource : resources) {
            totalResourcesSize += resource.size;
        }
    }

    private boolean terminate;

    @WorkerThread
    public void download(
            final String directory,
            final ProgressCallback progressCallback,
            final UseCaseCallback useCaseCallback
    ) {
        final long[] cnt = new long[1];
        terminate = false;
        for (WebResource resource : resources) {
            if (interruption.active) {
                return;
            }
            if (terminate) {
                return;
            }
            new DownloadResourceUseCase(resource, gameInstanceId).download(
                    directory,
                    new ProgressCallback() {
                        @Override
                        public void onProgress(long loadedSize, long totalResourceSize) {
                            if (progressCallback != null)
                                progressCallback.onProgress(
                                        cnt[0] + loadedSize,
                                        totalResourcesSize
                                );
                        }
                    },
                    new UseCaseCallback<Void>() {
                        @Override
                        public void onError(String message) {
                            terminate = true;
                            useCaseCallback.onError(message);
                        }

                        @Override
                        public void onSuccess(Void result) {

                        }
                    }
            );
            cnt[0] = cnt[0] + resource.size;
        }
        useCaseCallback.onSuccess(null);
    }
}
