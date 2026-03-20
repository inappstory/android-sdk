package com.inappstory.sdk.stories.cache;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.lrudiskcache.CacheSize;
import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;
import com.inappstory.sdk.stories.cache.vod.EmptyVODCacheJournal;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilesDownloadManager {

    private class FileDownloadKey {
        public String url() {
            return url;
        }

        public String filePath() {
            return filePath;
        }

        private final String url;
        private final String filePath;

        private FileDownloadKey(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FileDownloadKey)) return false;
            FileDownloadKey that = (FileDownloadKey) o;
            return Objects.equals(url, that.url) &&
                    Objects.equals(filePath, that.filePath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, filePath);
        }
    }

    public LruCachesHolder getCachesHolder() {
        return cachesHolder;
    }

    public VODCacheJournal getVodCacheJournal() {
        return vodCacheJournal;
    }

    public VODCacheJournal vodCacheJournal = new EmptyVODCacheJournal();

    private final ExecutorService initExecutor = Executors.newFixedThreadPool(1);

    private LruCachesHolder cachesHolder;
    private final DownloadThreadsHolder downloadThreadsHolder;

    private final Map<FileDownloadKey, List<FinishDownloadFileCallback>> downloadFileCallbacks = new HashMap<>();

    private final Object finishLock = new Object();

    public boolean addFinishCallback(String url, String outputFile, FinishDownloadFileCallback callback) {
        boolean isNewUrl = true;
        FileDownloadKey key = new FileDownloadKey(url, outputFile);
        synchronized (finishLock) {
            if (!downloadFileCallbacks.containsKey(key)) {
                downloadFileCallbacks.put(
                        key,
                        new ArrayList<FinishDownloadFileCallback>()
                );
            } else {
                isNewUrl = false;
            }
            downloadFileCallbacks.get(key).add(callback);
            return isNewUrl;
        }
    }

    public void invokeFinishCallbacks(String url, String outputFile, DownloadFileState state) {
        List<FinishDownloadFileCallback> callbacks = new ArrayList<>();
        FileDownloadKey key = new FileDownloadKey(url, outputFile);
        synchronized (finishLock) {
            if (downloadFileCallbacks.containsKey(key)) {
                callbacks.addAll(downloadFileCallbacks.remove(key));
            }
        }
        for (FinishDownloadFileCallback callback : callbacks) {
            callback.finish(state);
        }
    }

    public FilesDownloadManager(final IASCore core) {
        cachesHolder = new LruCachesHolder(core, core.appContext(), CacheSize.MEDIUM);

        initExecutor.submit(new Runnable() {
            @Override
            public void run() {
                File file = new File(
                        core.appContext().getFilesDir() +
                                File.separator +
                                "ias" +
                                File.separator +
                                "vod",
                        "vod_journal.bin"
                );
                vodCacheJournal = new VODCacheJournal();
                vodCacheJournal.initCacheJournal(file);
            }
        });

        downloadThreadsHolder = new DownloadThreadsHolder();
    }

    public void useFastDownloader(Runnable runnable) {
        downloadThreadsHolder.useFastCacheDownloader(runnable);
    }

    public void useCustomDownloader(Runnable runnable) {
        downloadThreadsHolder.useCustomDownloader(runnable);
    }
}
