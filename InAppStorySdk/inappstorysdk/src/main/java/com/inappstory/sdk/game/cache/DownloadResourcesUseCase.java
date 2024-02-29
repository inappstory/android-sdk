package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.utils.ProgressCallback;

import java.util.List;

public class DownloadResourcesUseCase {
    private List<WebResource> resources;
    private long totalResourcesSize;

    private String gameInstanceId;

    public DownloadResourcesUseCase(List<WebResource> resources, String gameInstanceId) {
        this.resources = resources;
        this.gameInstanceId = gameInstanceId;
        if (resources == null) return;
        totalResourcesSize = 0;
        for (WebResource resource : resources) {
            totalResourcesSize += resource.size;
        }
    }


    private boolean terminate;

    @WorkerThread
    void download(
            final String directory,
            final ProgressCallback progressCallback,
            final UseCaseCallback useCaseCallback
    ) {
        final long[] cnt = new long[1];
        terminate = false;
        for (WebResource resource : resources) {
            if (terminate) {
                return;
            }
            new DownloadResourceUseCase(resource, gameInstanceId).download(
                    directory,
                    new ProgressCallback() {
                        @Override
                        public void onProgress(long loadedSize, long totalResourceSize) {
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
