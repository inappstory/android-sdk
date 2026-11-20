package com.inappstory.sdk.stories.cache.usecases;

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
        if (fileState == null || !fileState.isFullDownload()) {
            downloadLog.sendRequestLog();
            downloadLog.generateResponseLog(false, filePath);
            core
                    .contentLoader()
                    .downloadThreads()
                    .useCustomDownloader(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                                    @Override
                                    public void finish(DownloadFileState fileState) {
                                        downloadLog.sendResponseLog();
                                        if (fileState == null ||  !fileState.isFullDownload()) {
                                            getFileCallback.onError(new SimpleUseCaseError(""));
                                            return;
                                        }
                                        long maxSize = Math.max(fileState.totalSize, fileState.downloadedSize);
                                        CacheJournalItem cacheJournalItem = generateCacheItem(
                                                maxSize, fileState.downloadedSize
                                        );
                                        try {
                                            getCache().put(cacheJournalItem);
                                        } catch (Exception e) {

                                            getFileCallback.onError(new SimpleUseCaseError(e.getMessage()));
                                        }
                                        getFileCallback.onSuccess(new File(filePath));
                                    }

                                    @Override
                                    public void waiting() {

                                    }
                                };
                                core.filesDownloader()
                                        .downloadFile(
                                                url,
                                                new File(filePath),
                                                null,
                                                downloadLog.responseLog,
                                                null,
                                                callback
                                        );

                            } catch (Exception e) {
                                getFileCallback.onError(new SimpleUseCaseError(e.getMessage()));
                            }
                        }
                    });
        } else {
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            getFileCallback.onSuccess(new File(filePath));
        }
        return null;
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getInfiniteCache();
    }
}
