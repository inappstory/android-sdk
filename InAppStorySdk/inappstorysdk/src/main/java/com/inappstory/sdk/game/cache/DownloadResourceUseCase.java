package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;

public class DownloadResourceUseCase {
    private WebResource resource;

    public DownloadResourceUseCase(WebResource resource) {
        this.resource = resource;
    }

    @WorkerThread
    void download(
            final String directory,
            final DownloadInterruption interruption,
            final ProgressCallback progressCallback,
            final UseCaseCallback<Void> useCaseCallback
    ) {
        try {
            String url = resource.url;
            String fileName = resource.key;
            final File resourceFile = new File(directory + File.separator + fileName);
            IASCore.getInstance().filesRepository.getGameResource(
                    url,
                    fileName,
                    resourceFile.getAbsolutePath(),
                    resource.size,
                    resource.sha1,
                    new IFileDownloadCallback() {
                        @Override
                        public void onSuccess(String fileAbsolutePath) {
                            if (progressCallback != null)
                                progressCallback.onProgress(resource.size, resource.size);
                            useCaseCallback.onSuccess(null);
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            useCaseCallback.onError(error);
                        }
                    },
                    new IFileDownloadProgressCallback() {
                        @Override
                        public void onProgress(long currentProgress, long max) {
                            if (progressCallback != null)
                                progressCallback.onProgress(currentProgress, max);
                        }
                    },
                    interruption
            );
        } catch (Exception e) {
            e.printStackTrace();
            useCaseCallback.onError(e.getMessage());
        }
    }
}
