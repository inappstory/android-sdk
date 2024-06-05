package com.inappstory.sdk.stories.cache;

import android.content.Context;

import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesDownloadManager {

    public LruCachesHolder getCachesHolder() {
        return cachesHolder;
    }

    public VODCacheJournal vodCacheJournal = new VODCacheJournal();

    private final LruCachesHolder cachesHolder;
    private final DownloadThreadsHolder downloadThreadsHolder;

    private final Map<String, List<FinishDownloadFileCallback>> downloadFileCallbacks = new HashMap<>();

    private final Object finishLock = new Object();

    public boolean addFinishCallback(String url, FinishDownloadFileCallback callback) {
        boolean isNewUrl = true;
        synchronized (finishLock) {
            if (!downloadFileCallbacks.containsKey(url)) {
                downloadFileCallbacks.put(
                        url,
                        new ArrayList<FinishDownloadFileCallback>()
                );
            } else {
                isNewUrl = false;
            }
            downloadFileCallbacks.get(url).add(callback);
            return isNewUrl;
        }
    }

    public void invokeFinishCallbacks(String url, DownloadFileState state) {
        List<FinishDownloadFileCallback> callbacks = new ArrayList<>();
        synchronized (finishLock) {
            if (downloadFileCallbacks.containsKey(url)) {
                callbacks.addAll(downloadFileCallbacks.remove(url));
            }
        }
        for (FinishDownloadFileCallback callback : callbacks) {
            callback.finish(state);
        }
    }

    public FilesDownloadManager(Context context, int cacheSize) {
        cachesHolder = new LruCachesHolder(context, cacheSize);
        downloadThreadsHolder  = new DownloadThreadsHolder();
    }

    public void useFastDownloader(Runnable runnable) {
        downloadThreadsHolder.useFastCacheDownloader(runnable);
    }

    public void useBundleDownloader(Runnable runnable) {
        downloadThreadsHolder.useBundleDownloader(runnable);
    }
}
