package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class CustomFileUseCase extends GetCacheFileUseCase<Void> {
    UseCaseCallback<File> getFileCallback;
    String url;

    public CustomFileUseCase(
            IASCore core,
            String url,
            UseCaseCallback<File> getFileCallback
    ) {
        super(core);
        this.getFileCallback = getFileCallback;
        this.url = url;
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = new FilePathCacheGenerator(
                url,
                core,
                FilePathCacheType.CUSTOM_FILE
        ).generate();
    }

    @Override
    public Void getFile() {
        downloadLog.generateRequestLog(url);
        DownloadFileState fileState = getCache().get(uniqueKey);

        if (fileState != null && fileState.isFullDownload()) {
            useLocalCopy();
        } else {
            downloadFile();
        }
        return null;
    }

    void useLocalCopy() {
        downloadLog.generateResponseLog(true, filePath);
        downloadLog.sendRequestResponseLog();
        getFileCallback.onSuccess(new File(filePath));
    }

    void downloadFile() {
        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);

        try {
            core.filesDownloader().downloadFile(
                    url,
                    new File(filePath),
                    null,
                    downloadLog.responseLog,
                    null,
                    0L,
                    createFinishCallback()
            );
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to initiate custom file download. Error: " + e.getMessage()
            );
            getFileCallback.onError(new SimpleUseCaseError("Failed to start download: " + e.getMessage()));
        }
    }

    FinishDownloadFileCallback createFinishCallback() {
        return new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState state) {
                downloadLog.sendResponseLog();

                if (state == null || !state.isFullDownload()) {
                    getFileCallback.onError(new SimpleUseCaseError("Download interrupted or incomplete"));
                    return;
                }

                long maxSize = Math.max(state.totalSize, state.downloadedSize);
                CacheJournalItem cacheJournalItem = generateCacheItem(maxSize, state.downloadedSize);

                try {
                    getCache().put(cacheJournalItem);
                    getFileCallback.onSuccess(new File(filePath));
                } catch (Exception e) {
                    InAppStoryManager.showDLog(
                            LoggerTags.IAS_WARN_TAG,
                            "Failed to update cache journal for custom file. Error: " + e.getMessage()
                    );
                    getFileCallback.onError(new SimpleUseCaseError("Failed to save file metadata: " + e.getMessage()));
                }
            }

            @Override
            public void waiting() {
            }
        };
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
