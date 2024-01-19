package com.inappstory.sdk.game.cache;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.List;

public class DownloadResourceUseCase {
    private WebResource resource;

    public DownloadResourceUseCase(WebResource resource) {
        this.resource = resource;
    }

    @WorkerThread
    void download(
            final String directory,
            final ProgressCallback progressCallback,
            final UseCaseCallback<Void> useCaseCallback
    ) {
        FileChecker fileChecker = new FileChecker();
        try {
            final String url = resource.url;
            final String fileName = resource.key;
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
            InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) throws Exception {
                    Downloader.downloadOrGetResourceFile(
                            url,
                            fileName,
                            service.getInfiniteCache(),
                            resourceFile,
                            new FileLoadProgressCallback() {
                                @Override
                                public void onSuccess(File file) {

                                }

                                @Override
                                public void onError(String error) {

                                }

                                @Override
                                public void onProgress(long loadedSize, long totalSize) {
                                    if (progressCallback != null)
                                        progressCallback.onProgress(loadedSize, totalSize);
                                }
                            });
                }
            });

            fileChecker.checkWithShaAndSize(
                    resourceFile,
                    resource.size,
                    resource.sha1,
                    true
            );
            if (progressCallback != null)
                progressCallback.onProgress(resource.size, resource.size);
            useCaseCallback.onSuccess(null);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
            useCaseCallback.onError(e.getMessage());
        }
    }
}
