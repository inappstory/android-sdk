package com.inappstory.sdk.stories.cache.usecases;

import android.util.Log;

import com.inappstory.sdk.game.cache.UseCaseCallback;
import com.inappstory.sdk.lrudiskcache.CacheJournalItem;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.SessionAsset;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FilesDownloadManager;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SessionAssetUseCase extends GetCacheFileUseCase<Void> {
    private final SessionAsset cacheObject;
    private final UseCaseCallback<File> useCaseCallback;

    public SessionAssetUseCase(
            FilesDownloadManager filesDownloadManager,
            UseCaseCallback<File> useCaseCallback,
            SessionAsset cacheObject
    ) {
        super(filesDownloadManager);
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

    private void logSessionAsset(String logMessage) {
        //  Log.e("SessionAsset", logMessage);

    }

    private void downloadFile() {
        //   Log.e("SessionAssetsIsReady", cacheObject.url + " Download");
        downloadLog.sendRequestLog();
        downloadLog.generateResponseLog(false, filePath);
        FinishDownloadFileCallback callback = new FinishDownloadFileCallback() {
            @Override
            public void finish(DownloadFileState fileState) {
                // logSessionAsset(cacheObject.url + " Download callback");
                if (fileState == null) {
                    useCaseCallback.onError("Can't download bundle file: " + cacheObject.url);
                    return;
                }

                //        Log.e("SessionAssetsIsReady", cacheObject.url + " Success");
                useCaseCallback.onSuccess(fileState.file);
            }
        };
        if (filesDownloadManager.addSecondFinishCallbackIfIsNew(
                cacheObject.url,
                new FinishDownloadFileCallback() {
                    @Override
                    public void finish(DownloadFileState fileState) {
                        //            Log.e("SessionAssetsIsReady", cacheObject.url + " FirstCallback");
                        logSessionAsset(cacheObject.url + " Download finished: " + fileState);
                        downloadLog.sendResponseLog();
                        CacheJournalItem cacheJournalItem = generateCacheItem();
                        cacheJournalItem.setSize(fileState.totalSize);
                        cacheJournalItem.setDownloadedSize(fileState.totalSize);
                        try {
                            getCache().put(cacheJournalItem);
                        } catch (IOException e) {
                            logSessionAsset(cacheObject.url + " Cache put error");
                        }
                    }
                },
                callback
        )) {
            try {
                long time = System.currentTimeMillis();
               // Log.e("SessionAssetsDownloadU", cacheObject.url + " " + System.currentTimeMillis() + " load");
                File file = new File(filePath);
                Downloader.downloadFileWithLogs(
                        cacheObject.url,
                        file,
                        null,
                        downloadLog.responseLog,
                        null,
                        filesDownloadManager
                );

                Log.e("SessionAssetsDownloadU", cacheObject.url + " Download time: " + (System.currentTimeMillis()-time) + "; File length: " + file.length());
            } catch (Exception e) {
                useCaseCallback.onError(e.getMessage());
            }
        }
    }

    @Override
    public Void getFile() {
        logSessionAsset(cacheObject.url + " getFile");
        getLocalFile(new Runnable() {
            @Override
            public void run() {

                downloadFile();

            }
        });
        return null;
    }

    private void getLocalFile(final Runnable error) {
        downloadLog.generateRequestLog(cacheObject.url);

        CacheJournalItem cached = getCache().getJournalItem(uniqueKey);
        DownloadFileState fileState = null;
        if (cached != null) {
            if (Objects.equals(cached.getSha1(), cacheObject.sha1)) {
                fileState = getCache().get(uniqueKey);
            } else {
                logSessionAsset(cacheObject.url + " SHA1 problem");
                deleteCacheKey();
            }
        }
        if (fileState != null) {
            File file = fileState.getFullFile();
            if (file != null) {
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                useCaseCallback.onSuccess(file);
                //logSessionAsset(cacheObject.url + " Local cache success");
                return;
            } else {
                deleteCacheKey();
            }
        }
        //  logSessionAsset(cacheObject.url + " Local cache error");
        error.run();
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
        return filesDownloadManager.getCachesHolder().getInfiniteCache();
    }
}
