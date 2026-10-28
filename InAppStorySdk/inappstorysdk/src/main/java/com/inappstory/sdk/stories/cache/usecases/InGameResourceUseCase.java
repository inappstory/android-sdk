package com.inappstory.sdk.stories.cache.usecases;


import android.system.Os;

import com.inappstory.sdk.core.IASCore;
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
import com.inappstory.sdk.utils.ProgressCallback;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InGameResourceUseCase extends GetCacheFileUseCase<Void> {
    private final WebResource resource;
    private final UseCaseCallback<Void> useCaseCallback;
    private final FileChecker fileChecker = new FileChecker();

    private String getArchiveName(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        String[] nameParts = fName.split("_");
        if (nameParts.length > 0) return nameParts[0];
        return "";
    }

    private List<String> symlinkKeys = new ArrayList<>();
    private String symlinkDir = null;

    public InGameResourceUseCase(
            IASCore core,
            String zipUrl,
            String gameInstanceId,
            UseCaseCallback<Void> useCaseCallback,
            WebResource resource
    ) {
        super(core);
        this.useCaseCallback = useCaseCallback;
        this.uniqueKey = StringsUtils.md5(gameInstanceId + "_" + resource.url);
        this.resource = resource;
        String realPath = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator + "gameResources" +
                File.separator +
                StringsUtils.md5(resource.url);

        symlinkDir = getCache().getCacheDir().getAbsolutePath() +
                File.separator +
                "v2" +
                File.separator +
                "zip" +
                File.separator +
                getArchiveName(zipUrl) +
                File.separator +
                StringsUtils.md5(zipUrl) +
                File.separator +
                "resources_" +
                gameInstanceId +
                File.separator;
        symlinkKeys.add(symlinkDir + resource.key);
        this.filePath = realPath;
    }

    private void createSymlinks() {
        try {
            new File(symlinkDir).mkdirs();
        } catch (Exception e) {

        }
        for (String symlinkKey: symlinkKeys) {
            try {
                Os.remove(symlinkKey);
            } catch (Exception e) {

            }
            try {
                Os.symlink(filePath, symlinkKey);
            } catch (Exception e) {
                filePath = symlinkKey;
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean getLocalResource() {

        downloadLog.generateRequestLog(resource.url);
        LruDiskCache cache = getCache();
        File cachedResource = cache.getFullFile(uniqueKey);
        if (cachedResource != null) {
            if (fileChecker.checkWithShaAndSize(
                    cachedResource,
                    resource.size,
                    resource.sha1,
                    true
            ) instanceof FileCheckerSuccess) {
                useCaseCallback.onSuccess(null);
                downloadLog.generateResponseLog(true, filePath);
                downloadLog.sendRequestResponseLog();
                return true;
            }
        }
        File resourceFile = new File(filePath);
        if (fileChecker.checkWithShaAndSize(
                resourceFile,
                resource.size,
                resource.sha1,
                true
        ) instanceof FileCheckerSuccess) {
            useCaseCallback.onSuccess(null);
            try {
                CacheJournalItem cacheJournalItem = generateCacheItem();
                cacheJournalItem.setDownloadedSize(cacheJournalItem.getSize());
                getCache().put(cacheJournalItem);
            } catch (IOException ignored) {

            }
            downloadLog.generateResponseLog(true, filePath);
            downloadLog.sendRequestResponseLog();
            return true;
        }
        return false;
    }

    @Override
    public Void getFile() {
        createSymlinks();
        if (!getLocalResource())
            downloadResource();
        return null;
    }

    private void downloadResource() {
        try {
            if (resource.url == null ||
                    resource.url.isEmpty() ||
                    resource.key == null ||
                    resource.key.isEmpty()
            ) {
                useCaseCallback.onError(new SimpleUseCaseError("Wrong resource key or url"));
                return;
            }
            long offset = 0;
            DownloadFileState fileState = getCache().get(uniqueKey);
            if (fileState != null) {
                offset = fileState.downloadedSize;
            }
            try {
                downloadLog.sendRequestLog();
                downloadLog.generateResponseLog(false, filePath);
                FinishDownloadFileCallback callback =
                        new FinishDownloadFileCallback() {
                            @Override
                            public void finish(DownloadFileState fileState) {
                                downloadLog.sendResponseLog();
                                if (fileState == null || fileState.downloadedSize != fileState.totalSize) {
                                    useCaseCallback.onError(new SimpleUseCaseError("Download interrupted"));
                                } else {
                                    FileCheckerResult checkerResult = fileChecker.checkWithShaAndSize(
                                            fileState.file,
                                            resource.size,
                                            resource.sha1,
                                            true
                                    );
                                    if (checkerResult instanceof FileCheckerSuccess) {
                                        CacheJournalItem cacheJournalItem = generateCacheItem();
                                        cacheJournalItem.setSize(fileState.totalSize);
                                        cacheJournalItem.setDownloadedSize(fileState.totalSize);
                                        try {
                                            getCache().put(cacheJournalItem);
                                        } catch (IOException e) {
                                            useCaseCallback.onError(new SimpleUseCaseError(e.getMessage()));
                                        }
                                        useCaseCallback.onSuccess(null);
                                    } else {
                                        useCaseCallback.onError(
                                                new SimpleUseCaseError("Wrong size or sha1 for url " + resource.url + ": " + checkerResult)
                                        );
                                    }
                                }
                            }

                            @Override
                            public void waiting() {

                            }
                        };
                core
                        .contentLoader()
                        .downloader()
                        .downloadFile(
                                resource.url,
                                new File(filePath),
                                new FileLoadProgressCallback() {
                                    @Override
                                    public void onSuccess(File file) {

                                    }

                                    @Override
                                    public void onError(String error) {

                                    }

                                    @Override
                                    public void onProgress(long loadedSize, long totalSize) {
                                    }
                                },
                                downloadLog.responseLog,
                                null,
                                offset,
                                -1,
                                callback
                        );
            } catch (Exception e) {
                useCaseCallback.onError(new SimpleUseCaseError(e.getMessage()));
                e.printStackTrace();
            }
        } catch (Exception e) {
            core.exceptionManager().createExceptionLog(e);
            useCaseCallback.onError(new SimpleUseCaseError(e.getMessage()));
        }
    }

    @Override
    protected CacheJournalItem generateCacheItem() {
        return new CacheJournalItem(
                uniqueKey,
                filePath,
                null,
                null,
                resource.sha1,
                resource.key,
                System.currentTimeMillis(),
                resource.size,
                0,
                null
        );
    }

    @Override
    protected LruDiskCache getCache() {
        return core.contentLoader().getInfiniteCache();
    }
}
