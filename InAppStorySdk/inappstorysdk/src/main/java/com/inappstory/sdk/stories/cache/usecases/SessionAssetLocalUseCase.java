package com.inappstory.sdk.stories.cache.usecases;

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
import java.io.IOException;
import java.util.Objects;

public class SessionAssetLocalUseCase extends GetCacheFileUseCase<Void> {
    private final SessionAsset cacheObject;
    private final UseCaseCallback<File> useCaseCallback;

    public SessionAssetLocalUseCase(
            IASCore core,
            UseCaseCallback<File> useCaseCallback,
            SessionAsset cacheObject
    ) {
        super(core);
        this.cacheObject = cacheObject;
        this.useCaseCallback = useCaseCallback;
        this.uniqueKey = StringsUtils.md5(cacheObject.filename);
        this.filePath = new FilePathCacheGenerator(
                cacheObject.filename,
                core,
                FilePathCacheType.SESSION_ASSET_LOCAL
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

    @Override
    public Void getFile() {
        getLocalFile();
        return null;
    }

    private boolean getLocalFile() {
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
                useCaseCallback.onSuccess(file);
                return true;
            } else {
                deleteCacheKey();
            }
        }
        useCaseCallback.onError(new SimpleUseCaseError("No local file"));
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
