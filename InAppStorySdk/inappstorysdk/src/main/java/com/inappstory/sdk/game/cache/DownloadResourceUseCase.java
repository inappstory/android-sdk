package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.GameResourceDownload;
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
        final FileChecker fileChecker = new FileChecker();
        try {
            String url = resource.url;
            String fileName = resource.key;
            if (url == null || url.isEmpty() || fileName == null || fileName.isEmpty()) {
                useCaseCallback.onError("Wrong resource key or url");
                return;
            }
            final File resourceFile = new File(directory + File.separator + fileName);
            if (fileChecker.checkWithShaAndSize(
                    resourceFile,
                    resource.size,
                    resource.sha1,
                    true
            )) {
                if (progressCallback != null)
                    progressCallback.onProgress(resource.size, resource.size);
                useCaseCallback.onSuccess(null);
            }
            new GameResourceDownload(
                    url,
                    fileName,
                    resourceFile.getAbsolutePath(),
                    new IFileDownloadCallback() {
                        @Override
                        public void onSuccess(String fileAbsolutePath) {
                            fileChecker.checkWithShaAndSize(
                                    resourceFile,
                                    resource.size,
                                    resource.sha1,
                                    true
                            );
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
            ).downloadOrGetFromCache();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
            useCaseCallback.onError(e.getMessage());
        }
    }
}
