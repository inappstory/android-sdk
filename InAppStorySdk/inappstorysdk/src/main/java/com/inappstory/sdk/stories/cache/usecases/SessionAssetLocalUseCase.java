package com.inappstory.sdk.stories.cache.usecases;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.utils.format.StringsUtils;

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
        useCaseCallback.onError("No local file");
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
        return core.contentLoader().getBundleCache();
    }
}
