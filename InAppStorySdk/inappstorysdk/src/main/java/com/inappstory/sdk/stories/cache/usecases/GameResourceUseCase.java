package com.inappstory.sdk.stories.cache.usecases;


import android.system.Os;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.GameNameHolder;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.FileCheckerResult;
import com.inappstory.sdk.lrudiskcache.FileCheckerSuccess;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameResourceUseCase extends GetCacheFileUseCase<Void> {
    private final WebResource resource;
    private final ProgressCallback progressCallback;
    private final DownloadInterruption interruption;
    private final UseCaseCallback<Void> useCaseCallback;
    private final FileChecker fileChecker;



    private final List<String> symlinkKeys = new ArrayList<>();
    private String symlinkDir = null;

    public GameResourceUseCase(
            IASCore core,
            FileChecker fileChecker,
            String symlinkDir,
            String gameInstanceId,
            ProgressCallback progressCallback,
            DownloadInterruption interruption,
            UseCaseCallback<Void> useCaseCallback,
            List<WebResource> resources
    ) {
        super(core);
        if (resources == null || resources.isEmpty()) throw new RuntimeException("");
        WebResource resource = resources.get(0);
        this.fileChecker = fileChecker;
        this.useCaseCallback = useCaseCallback;
        this.interruption = interruption;
        this.progressCallback = progressCallback;
        this.uniqueKey = StringsUtils.md5(gameInstanceId + "_" + resource.url);
        this.resource = resource;
        this.symlinkDir = symlinkDir;
        for (WebResource webResource : resources) {
            symlinkKeys.add(symlinkDir + webResource.key);
        }
        this.filePath = new FilePathCacheGenerator(
                resource.url,
                core,
                FilePathCacheType.GAME_RESOURCE
        ).generate();
    }

    @Override
    public Void getFile() {
        createSymlinks();
        if (!getLocalResource())
            downloadResource();
        return null;
    }

    private void createSymlinks() {
        try {
            new File(symlinkDir).mkdirs();
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to create symlink directory. Error: " + e.getMessage()
            );
        }
        for (String symlinkKey : symlinkKeys) {
            try {
                Os.remove(symlinkKey);
            } catch (Exception e) {

            }
            try {
                Os.symlink(filePath, symlinkKey);
            } catch (Exception e) {
                InAppStoryManager.showDLog(
                        LoggerTags.IAS_WARN_TAG,
                        "Symlink creation failed, falling back to direct path. Error: " + e.getMessage()
                );
                filePath = symlinkKey;
                break;
            }
        }
    }


    private boolean getLocalResource() {

        downloadLog.generateRequestLog(resource.url);
        LruDiskCache cache = getCache();
        File cachedResource = cache.getFullFile(uniqueKey);

        if (cachedResource != null) {
            if (fileChecker.checkWithShaAndSize(cachedResource, resource.size, resource.sha1, true) instanceof FileCheckerSuccess) {
                handleLocalResource(cachedResource.getAbsolutePath());
                return true;
            }
        }

        File resourceFile = new File(filePath);
        if (fileChecker.checkWithShaAndSize(resourceFile, resource.size, resource.sha1, true) instanceof FileCheckerSuccess) {
            handleLocalResource(filePath);
            updateCacheJournal(resource.size, resource.size);
            return true;
        }

        return false;
    }

    private void handleLocalResource(String successPath) {
        progressCallback.onProgress(resource.size, resource.size);
        useCaseCallback.onSuccess(null);
        downloadLog.generateResponseLog(true, successPath);
        downloadLog.sendRequestResponseLog();
    }

    private void updateCacheJournal(long size, long downloadedSize) {
        try {
            CacheJournalItem cacheJournalItem = generateCacheItem(size, downloadedSize);
            getCache().put(cacheJournalItem);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal for local resource. Error: " + e.getMessage()
            );
        }
    }

    private void downloadResource() {
        if (!validateResource()) {
            return;
        }

        long offset = getDownloadOffset();
        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);

        try {
            core.filesDownloader().downloadFile(
                    resource.url,
                    new File(filePath),
                    createProgressCallback(),
                    downloadLog.responseLog,
                    interruption,
                    offset,
                    createFinishCallback()
            );
        } catch (Exception e) {
            core.exceptionManager().createExceptionLog(e);
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to initiate resource download. Error: " + e.getMessage()
            );
            useCaseCallback.onError(new SimpleUseCaseError("Failed to start download: " + e.getMessage()));
        }
    }

    private boolean validateResource() {
        if (resource.url == null || resource.url.isEmpty() ||
                resource.key == null || resource.key.isEmpty()) {
            useCaseCallback.onError(new SimpleUseCaseError("Wrong resource key or url"));
            return false;
        }
        return true;
    }

    private FinishDownloadFileCallback createFinishCallback() {
        return new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState state) {
                downloadLog.sendResponseLog();
                handleDownloadCompletion(state);
            }

            @Override
            public void waiting() {
            }
        };
    }

    private void handleDownloadCompletion(DownloadFileState state) {
        if (state == null || !state.isFullDownload()) {
            useCaseCallback.onError(new SimpleUseCaseError("Download interrupted"));
            return;
        }

        FileCheckerResult checkerResult = fileChecker.checkWithShaAndSize(
                state.file,
                resource.size,
                resource.sha1,
                true
        );

        if (checkerResult instanceof FileCheckerSuccess) {
            finalizeSuccessfulDownload(state);
        } else {
            useCaseCallback.onError(new SimpleUseCaseError(
                    "Wrong size or sha1 for url " + resource.url + ": " + checkerResult
            ));
        }
    }

    private void finalizeSuccessfulDownload(DownloadFileState state) {
        long maxSize = Math.max(state.totalSize, state.downloadedSize);
        CacheJournalItem cacheJournalItem = generateCacheItem(maxSize, state.downloadedSize);

        try {
            getCache().put(cacheJournalItem);
            progressCallback.onProgress(resource.size, resource.size);
            useCaseCallback.onSuccess(null);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal after download. Error: " + e.getMessage()
            );
            useCaseCallback.onError(new SimpleUseCaseError("Failed to save file metadata: " + e.getMessage()));
        }
    }

    private FileLoadProgressCallback createProgressCallback() {
        return new FileLoadProgressCallback() {
            @Override
            public void onProgress(long loadedSize, long totalSize) {
                progressCallback.onProgress(loadedSize, totalSize);
            }

            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onError(String error) {
                useCaseCallback.onError(new SimpleUseCaseError(error));
            }
        };
    }

    private long getDownloadOffset() {
        DownloadFileState fileState = getCache().get(uniqueKey);
        return (fileState != null) ? fileState.downloadedSize : 0;
    }

    @Override
    protected String journalItemSha1() {
        return resource.sha1;
    }

    @Override
    protected String journalItemReplaceKey() {
        return resource.key;
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
