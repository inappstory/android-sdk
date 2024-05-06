package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;

public class GameResourceUseCase extends GetCacheFileUseCase<Void> {
    private final WebResource resource;
    private final ProgressCallback progressCallback;
    private final DownloadInterruption interruption;
    private final UseCaseCallback<Void> useCaseCallback;
    private final FileChecker fileChecker = new FileChecker();

    private String getArchiveName(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        String[] nameParts = fName.split("_");
        if (nameParts.length > 0) return nameParts[0];
        return "";
    }

    public GameResourceUseCase(
            FilesDownloadManager filesDownloadManager,
            String zipUrl,
            String gameInstanceId,
            ProgressCallback progressCallback,
            DownloadInterruption interruption,
            UseCaseCallback<Void> useCaseCallback,
            WebResource resource
    ) {
        super(filesDownloadManager);
        this.useCaseCallback = useCaseCallback;
        this.interruption = interruption;
        this.progressCallback = progressCallback;
        this.uniqueKey = StringsUtils.md5(gameInstanceId + "_" + resource.url);
        this.resource = resource;
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip" +
                File.separator +
                getArchiveName(zipUrl) +
                File.separator +
                StringsUtils.md5(zipUrl) +
                File.separator +
                "resources_" +
                gameInstanceId +
                File.separator +
                resource.key;
    }

    private boolean getLocalResource() {

        downloadLog.generateRequestLog(resource.url);
        LruDiskCache cache = getCache();
        File cachedResource = cache.getFullFile(uniqueKey);
        if (cachedResource != null) {
            if (fileChecker.checkWithShaAndSize(
                    cachedResource,
                    resource.size,
                    resource.sha1,
                    true
            )) {
                progressCallback.onProgress(resource.size, resource.size);
                useCaseCallback.onSuccess(null);
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                return true;
            }
        }
        File resourceFile = new File(filePath);
        if (fileChecker.checkWithShaAndSize(
                resourceFile,
                resource.size,
                resource.sha1,
                true
        )) {
            progressCallback.onProgress(resource.size, resource.size);
            useCaseCallback.onSuccess(null);
            try {
                CacheJournalItem cacheJournalItem = generateCacheItem();
                cacheJournalItem.setDownloadedSize(cacheJournalItem.getSize());
                getCache().put(cacheJournalItem);
            } catch (IOException ignored) {

            }
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            return true;
        }
        return false;
    }

    @Override
    public Void getFile() {
        if (getLocalResource()) return null;
        downloadResource();
        return null;
    }

    private void downloadResource() {
        try {
            if (resource.url == null ||
                    resource.url.isEmpty() ||
                    resource.key == null ||
                    resource.key.isEmpty()
            ) {
                useCaseCallback.onError("Wrong resource key or url");
                return;
            }
            long offset = 0;
            DownloadFileState fileState = getCache().get(uniqueKey);
            if (fileState != null) {
                offset = fileState.downloadedSize;
            }
            try {
                Log.e("ScenarioDownload", "Download: " + uniqueKey);
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                fileState = Downloader.downloadFile(
                        resource.url,
                        new File(filePath),
                        new FileLoadProgressCallback() {
                            @Override
                            public void onSuccess(File file) {

                            }

                            @Override
                            public void onError(String error) {

                            }

                            @Override
                            public void onProgress(long loadedSize, long totalSize) {
                                progressCallback.onProgress(loadedSize, totalSize);
                            }
                        },
                        downloadLog.responseLog,
                        interruption,
                        offset
                );
                downloadLog.sendResponseLog();
                if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                    useCaseCallback.onError("Download interrupted");
                } else {
                    if (fileChecker.checkWithShaAndSize(
                            fileState.file,
                            resource.size,
                            resource.sha1,
                            true
                    )) {
                        Log.e("ScenarioDownload", "Downloaded: " + uniqueKey);
                        CacheJournalItem cacheJournalItem = generateCacheItem();
                        cacheJournalItem.setSize(fileState.totalSize);
                        cacheJournalItem.setDownloadedSize(fileState.totalSize);
                        getCache().put(cacheJournalItem);
                        progressCallback.onProgress(resource.size, resource.size);
                        useCaseCallback.onSuccess(null);
                    } else {
                        useCaseCallback.onError("Wrong size or sha1");
                    }
                }
            } catch (Exception e) {
                useCaseCallback.onError(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
            useCaseCallback.onError(e.getMessage());
        }
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                null,
                resource.sha1,
                resource.key,
                System.currentTimeMillis(),
                resource.size,
                0
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
