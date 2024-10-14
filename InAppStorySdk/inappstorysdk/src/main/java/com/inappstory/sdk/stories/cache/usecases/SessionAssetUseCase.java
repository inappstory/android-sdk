package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SessionAssetUseCase extends GetCacheFileUseCase<Void> {
    private final SessionAsset cacheObject;
    private final UseCaseCallback<File> useCaseCallback;

    public SessionAssetUseCase(
            IASCore core,
            UseCaseCallback<File> useCaseCallback,
            SessionAsset cacheObject
    ) {
        super(core);
        this.cacheObject = cacheObject;
        this.useCaseCallback = useCaseCallback;
        this.uniqueKey = StringsUtils.md5(cacheObject.filename);
        this.filePath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "bundle" +
                File.separator +
                cacheObject.filename;
    }

    private void deleteCacheKey() {
        try {
            getCache().delete(uniqueKey);
        } catch (IOException e) {

        }
    }

    private void downloadFile() {
        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);
        core.contentLoader().filesDownloadManager()
                .useBundleDownloader(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
                                @Override
                                public void finish(DownloadFileState fileState) {
                                    downloadLog.sendResponseLog();
                                    if (fileState == null) {
                                        useCaseCallback.onError("Can't download bundle file: " + cacheObject.url);
                                        return;
                                    }
                                    CacheJournalItem cacheJournalItem = generateCacheItem();
                                    cacheJournalItem.setSize(fileState.totalSize);
                                    cacheJournalItem.setDownloadedSize(fileState.totalSize);
                                    try {
                                        getCache().put(cacheJournalItem);
                                    } catch (IOException e) {

                                    }
                                    useCaseCallback.onSuccess(fileState.file);
                                }
                            };
                            core
                                    .contentLoader()
                                    .downloader()
                                    .downloadFile(
                                            cacheObject.url,
                                            new File(filePath),
                                            null,
                                            downloadLog.responseLog,
                                            null,
                                            callback
                                    );

                        } catch (Exception e) {
                            useCaseCallback.onError(e.getMessage());
                        }
                    }
                });

    }

    @Override
    public Void getFile() {
        if (!getLocalFile())
            downloadFile();
        return null;
    }

    private boolean getLocalFile() {
        downloadLog.generateRequestLog(cacheObject.url);
        CacheJournalItem cached = getCache().getJournalItem(uniqueKey);
        DownloadFileState fileState = null;
        if (cached != null) {
            if (Objects.equals(cached.getSha1(), cacheObject.sha1)) {
                fileState = getCache().get(uniqueKey);
            } else {
                deleteCacheKey();
            }
        }
        if (fileState != null) {
            File file = fileState.getFullFile();
            if (file != null) {
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                useCaseCallback.onSuccess(file);
                return true;
            } else {
                deleteCacheKey();
            }
        }
        return false;
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                cacheObject.type,
                cacheObject.sha1,
                cacheObject.replaceKey,
                System.currentTimeMillis(),
                cacheObject.size,
                0,
                cacheObject.mimeType
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return core.contentLoader().getInfiniteCache();
    }
}
