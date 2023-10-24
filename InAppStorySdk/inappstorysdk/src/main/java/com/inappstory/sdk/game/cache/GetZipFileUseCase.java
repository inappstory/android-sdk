package com.inappstory.sdk.game.cache;

import static java.util.UUID.randomUUID;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.lrudiskcache.FileChecker;
import com.inappstory.sdk.core.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadProgressCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.ZipArchiveDownload;
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
    void get(
            final DownloadInterruption interruption,
            final @NonNull UseCaseCallback<File> callback,
            final ProgressCallback progressCallback,
            final long totalGameSize
    ) {
        InAppStoryService inAppStoryService = InAppStoryService.getInstance();
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
                        final String hash = randomUUID().toString();
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
                        ProfilingManager.getInstance().addTask("game_download", hash);
                        IASCoreManager.getInstance().filesRepository.getZipArchive(
                                url,
                                zipFile.getAbsolutePath(),
                                new IFileDownloadCallback() {
                                    @Override
                                    public void onSuccess(String fileAbsolutePath) {
                                        File file = new File(fileAbsolutePath);
                                        if (!fileChecker.checkWithShaAndSize(
                                                file,
                                                size,
                                                sha1,
                                                true
                                        )) {
                                            callback.onError("File sha or size is incorrect");
                                        } else {
                                            callback.onSuccess(file);
                                        }
                                        ProfilingManager.getInstance().setReady(hash);
                                    }

                                    @Override
                                    public void onError(int errorCode, String error) {
                                        callback.onError(error);
                                    }
                                },
                                new IFileDownloadProgressCallback() {
                                    @Override
                                    public void onProgress(long currentProgress, long max) {
                                        progressCallback.onProgress(currentProgress, max);
                                    }
                                },
                                interruption
                        );
                    }

                    @Override
                    public void onSuccess(File result) {
                        callback.onSuccess(result);
                    }
                });
    }


}
