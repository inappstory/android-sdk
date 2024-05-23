package com.inappstory.sdk.game.cache;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileChecker;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.utils.ProgressCallback;

import java.io.File;

public class GetZipFileUseCase extends GameNameHolder {
    String url;

    long size;
    String sha1;

    public GetZipFileUseCase(String url,
                             long size,
                             String sha1) {
        this.url = url;
        this.sha1 = sha1;
        this.size = size;
    }

    @WorkerThread
    void get() {
    }

    @WorkerThread
    public void get(
            final DownloadInterruption interruption,
            final @NonNull UseCaseCallback<File> callback,
            final ProgressCallback progressCallback,
            final long totalGameSize
    ) {
        final InAppStoryService inAppStoryService = InAppStoryService.getInstance();
        if (inAppStoryService == null) {
            callback.onError("InAppStory service is unavailable");
            return;
        }
        final FileChecker fileChecker = new FileChecker();
        final LruDiskCache cache = inAppStoryService.getInfiniteCache();
        new GetLocalZipFileUseCase(url, size, sha1).
                get(cache, new UseCaseCallback<File>() {
                    @Override
                    public void onError(String message) {
                        String hash = randomUUID().toString();
                        String gameName = getGameName(url);
                        File gameDir = new File(
                                cache.getCacheDir() +
                                        File.separator + "zip" +
                                        File.separator + gameName +
                                        File.separator
                        );
                        if (!gameDir.getAbsolutePath().startsWith(
                                cache.getCacheDir() +
                                        File.separator + "zip")) {
                            callback.onError("Error in game name");
                            return;
                        }
                        if (totalGameSize >
                                cache.getCacheDir().getFreeSpace()) {
                            callback.onError("No free space for download");
                            return;
                        }
                        File zipFile = new File(gameDir, url.hashCode() + ".zip");
                        DownloadFileState fileState = null;
                        ProfilingManager.getInstance().addTask("game_download", hash);
                        try {
                            fileState = Downloader.downloadOrGetFile(
                                    url,
                                    true,
                                    inAppStoryService.getInfiniteCache(),
                                    zipFile,
                                    new FileLoadProgressCallback() {
                                        @Override
                                        public void onProgress(long loadedSize, long totalSize) {
                                            progressCallback.onProgress(loadedSize, totalSize);
                                        }

                                        @Override
                                        public void onSuccess(File file) {

                                        }

                                        @Override
                                        public void onError(String error) {
                                            callback.onError(error);
                                        }
                                    },
                                    interruption,
                                    hash
                            );
                        } catch (Exception e) {
                            callback.onError(e.getMessage());
                        }
                        if (fileState != null && fileState.file != null &&
                                (fileState.downloadedSize == fileState.totalSize)) {
                            if (!fileChecker.checkWithShaAndSize(
                                    fileState.file,
                                    size,
                                    sha1,
                                    true
                            )) {
                                callback.onError("File sha or size is incorrect");
                            } else {
                                callback.onSuccess(fileState.file);
                            }
                            ProfilingManager.getInstance().setReady(hash);
                        } else {
                            callback.onError("File downloading was interrupted");
                        }
                    }

                    @Override
                    public void onSuccess(File result) {
                        callback.onSuccess(result);
                    }
                });
    }


}
