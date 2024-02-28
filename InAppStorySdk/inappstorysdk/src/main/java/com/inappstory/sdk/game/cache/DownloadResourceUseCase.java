package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;
import java.util.List;

public class DownloadResourceUseCase {
    private WebResource resource;
    private String gameInstanceId;

    public DownloadResourceUseCase(WebResource resource, String gameInstanceId) {
        this.resource = resource;
        this.gameInstanceId = gameInstanceId;

    }

    @WorkerThread
    void download(
            final String directory,
            final ProgressCallback progressCallback,
            final UseCaseCallback<Void> useCaseCallback
    ) {
        FileChecker fileChecker = new FileChecker();
        try {
            String url = resource.url;
            String fileName = resource.key;
            if (url == null || url.isEmpty() || fileName == null || fileName.isEmpty()) {
                useCaseCallback.onError("Wrong resource key or url");
                return;
            }
            File resourceFile = new File(directory + File.separator + fileName);
            if (fileChecker.checkWithShaAndSize(
                    resourceFile,
                    resource.size,
                    resource.sha1,
                    true
            )) {
                if (progressCallback != null)
                    progressCallback.onProgress(resource.size, resource.size);
                useCaseCallback.onSuccess(null);
                return;
            }
            String key = resource.sha1;
            if (key == null) key = FileManager.SHA1(resource.url);
            if (key == null) {
                useCaseCallback.onError(null);
                return;
            }
            InAppStoryService service = InAppStoryService.getInstance();
            if (service == null) return;
            Downloader.downloadOrGetResourceFile(
                    url,
                    gameInstanceId + "_" + key,
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
