package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.network.content.models.SessionAsset;
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
                        if (cacheObject.url.contains("widgets/poll.css")) {
                            InAppStoryManager.showDLog("downloadPollCss", "putToCacheSuccess " + cacheJournalItem.getUniqueKey());
                        }
                    } catch (IOException e) {
                        if (cacheObject.url.contains("widgets/poll.css")) {
                            InAppStoryManager.showDLog("downloadPollCss", "putToCacheError " + e.getMessage());
                        }
                    }
                    if (cacheObject.url.contains("widgets/poll.css")) {
                        InAppStoryManager.showDLog("downloadPollCss", "success");
                    }
                    useCaseCallback.onSuccess(fileState.file);
                }

                @Override
                public void waiting() {

                }
            };

            if (cacheObject.url.contains("widgets/poll.css")) {
                InAppStoryManager.showDLog("downloadPollCss", "downloadFile");
            }
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

    @Override
    public Void getFile() {
        if (!getLocalFile())
            downloadFile();
        return null;
    }

    private boolean getLocalFile() {

        if (cacheObject.url.contains("widgets/poll.css")) {
            InAppStoryManager.showDLog("downloadPollCss", "getLocalFile");
        }
        downloadLog.generateRequestLog(cacheObject.url);
        CacheJournalItem cached = getCache().getJournalItem(uniqueKey);
        DownloadFileState fileState = null;

        if (cached != null) {
            if (cacheObject.url.contains("widgets/poll.css")) {
                InAppStoryManager.showDLog("downloadPollCss",
                        uniqueKey + " " + cached.getSha1() + " " + cacheObject.sha1);
            }
            if (Objects.equals(cached.getSha1(), cacheObject.sha1)) {
                fileState = getCache().get(uniqueKey);
            } else {
                deleteCacheKey();
            }
        } else {
            if (cacheObject.url.contains("widgets/poll.css")) {
                InAppStoryManager.showDLog("downloadPollCss", "getLocalFile cached is null");
            }
        }

        if (fileState != null) {
            File file = fileState.getFullFile();
            if (file != null) {
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                useCaseCallback.onSuccess(file);
                if (cacheObject.url.contains("widgets/poll.css")) {
                    InAppStoryManager.showDLog("downloadPollCss", "getLocalFile has local file");
                }
                return true;
            } else {
                if (cacheObject.url.contains("widgets/poll.css")) {
                    InAppStoryManager.showDLog("downloadPollCss", "getLocalFile file is null");
                }
                deleteCacheKey();
            }
        } else {
            if (cacheObject.url.contains("widgets/poll.css")) {
                InAppStoryManager.showDLog("downloadPollCss", "getLocalFile fileState is null");
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
        return core.contentLoader().getBundleCache();
    }
}
