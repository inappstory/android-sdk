package com.inappstory.sdk.stories.cache.usecases;


import android.util.Pair;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.SimpleUseCaseError;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.FilePathCacheGenerator;
import com.inappstory.sdk.utils.FilePathCacheType;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.util.Objects;

public class SessionAssetUseCase extends GetCacheFileUseCase<Void> {
    private final SessionAsset cacheObject;
    private final UseCaseCallback<Pair<SessionAsset, File>> useCaseCallback;

    public SessionAssetUseCase(
            IASCore core,
            UseCaseCallback<Pair<SessionAsset, File>> useCaseCallback,
            SessionAsset cacheObject
    ) {
        super(core);
        this.cacheObject = cacheObject;
        this.useCaseCallback = useCaseCallback;
        this.uniqueKey = StringsUtils.md5(cacheObject.filename);
        this.filePath = new FilePathCacheGenerator(
                cacheObject.filename,
                core,
                FilePathCacheType.SESSION_ASSET
        ).generate();
    }

    private void deleteCacheKey() {
        try {
            getCache().delete(uniqueKey);
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to delete asset cache key. Error: " + e.getMessage()
            );
        }
    }

    private void downloadFile() {
        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);

        try {
            FinishDownloadFileCallback callback = createFinishCallback();
            core.filesDownloader()
                    .downloadFile(
                            cacheObject.url,
                            new File(filePath),
                            null,
                            downloadLog.responseLog,
                            null,
                            callback
                    );

        } catch (Exception e) {
            useCaseCallback.onError(new SimpleUseCaseError(e.getMessage()));
        }
    }

    private FinishDownloadFileCallback createFinishCallback() {
        return new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState state) {
                downloadLog.sendResponseLog();
                if (state == null || !state.isFullDownload()) {
                    useCaseCallback.onError(new SimpleUseCaseError("Can't download bundle file: " + cacheObject.url));
                    return;
                }
                if (updateCacheJournal(state)) {
                    useCaseCallback.onSuccess(new Pair<>(cacheObject, state.file));
                } else {
                    useCaseCallback.onError(new SimpleUseCaseError("Failed to save asset metadata to cache"));
                }
            }

            @Override
            public void waiting() {
            }
        };
    }

    private boolean updateCacheJournal(DownloadFileState state) {
        long maxSize = Math.max(state.totalSize, state.downloadedSize);
        CacheJournalItem cacheJournalItem = generateCacheItem(
                maxSize,
                state.downloadedSize
        );
        try {
            getCache().put(cacheJournalItem);
            return true;
        } catch (Exception e) {
            InAppStoryManager.showDLog(
                    LoggerTags.IAS_WARN_TAG,
                    "Failed to update cache journal for asset. Error: " + e.getMessage()
            );
            return false;
        }
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
                useCaseCallback.onSuccess(new Pair<>(cacheObject, file));

                return true;
            } else {
                deleteCacheKey();
            }
        }
        return false;
    }

    @Override
    protected String journalItemType() {
        return cacheObject.type;
    }

    @Override
    protected String journalItemSha1() {
        return cacheObject.sha1;
    }

    @Override
    protected String journalItemReplaceKey() {
        return cacheObject.replaceKey;
    }

    @Override
    protected String journalItemMimeType() {
        return cacheObject.mimeType;
    }


    @Override
    protected LruDiskCache getCache() {
        return core.cachesHolder().getBundleCache();
    }
}
