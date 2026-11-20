package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;

public class StoryCoverUseCase extends GetCacheFileUseCase<Void> {
    IGetStoryCoverCallback getStoryCoverCallback;
    String url;

    public StoryCoverUseCase(
            IASCore core,
            String url,
            IGetStoryCoverCallback getStoryCoverCallback
    ) {
        super(core);
        this.getStoryCoverCallback = getStoryCoverCallback;
        this.url = url;
        this.uniqueKey = StringsUtils.md5(url);
        this.filePath = new FilePathCacheGenerator(
                url,
                core,
                FilePathCacheType.STORY_COVER
        ).generate();
    }

    @Override
    public Void getFile() {
        downloadLog.generateRequestLog(url);
        DownloadFileState fileState = getCache().get(uniqueKey);
        if (fileState == null || !fileState.isFullDownload()) {
            downloadLog.sendRequestLog();
            downloadLog.generateResponseLog(false, filePath);
            core.contentLoader().downloadThreads()
                    .useFastCacheDownloader(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                                    @Override
                                    public void finish(DownloadFileState fileState) {
                                        downloadLog.sendResponseLog();
                                        if (fileState == null || !fileState.isFullDownload()) {
                                            getStoryCoverCallback.error();
                                            return;
                                        }
                                        CacheJournalItem cacheJournalItem = generateCacheItem(
                                                fileState.file.length(),
                                                fileState.file.length()
                                        );
                                        try {
                                            getCache().put(cacheJournalItem);
                                        } catch (Exception e) {

                                            getStoryCoverCallback.error();
                                        }
                                        getStoryCoverCallback.success(filePath);
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
                                getStoryCoverCallback.error();
                            }
                        }
                    });
        } else {
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            getStoryCoverCallback.success(filePath);
        }
        return null;
    }

    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getFastCache();
    }
}
