package com.inappstory.sdk.stories.cache;

import android.content.Context;
import android.util.Log;

import com.inappstory.sdk.lrudiskcache.LruCachesHolder;
import com.inappstory.sdk.stories.cache.usecases.FinishDownloadFileCallback;
import com.inappstory.sdk.stories.cache.vod.VODCacheJournal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesDownloadManager {

    public LruCachesHolder getCachesHolder() {
        return cachesHolder;
    }

    public VODCacheJournal getVodCacheJournal() {
        return vodCacheJournal;
    }

    public final VODCacheJournal vodCacheJournal;

    private final LruCachesHolder cachesHolder;
    private final DownloadThreadsHolder downloadThreadsHolder;

    private final Map<String, List<FinishDownloadFileCallback>> downloadFileCallbacks = new HashMap<>();

    private final Object finishLock = new Object();
    public void clearCallbacks() {
        synchronized (finishLock) {
            downloadFileCallbacks.clear();
        }
    }

    public boolean addSecondFinishCallbackIfIsNew(
            String url,
            FinishDownloadFileCallback callback,
            FinishDownloadFileCallback callback2
    ) {
        boolean isNewUrl = true;
        synchronized (finishLock) {
            List<FinishDownloadFileCallback> callbacksByUrl = downloadFileCallbacks.get(url);
            if (callbacksByUrl == null) {
                callbacksByUrl = new ArrayList<>();
                callbacksByUrl.add(callback);
                callbacksByUrl.add(callback2);
                downloadFileCallbacks.put(
                        url,
                        callbacksByUrl
                );
            } else {
                isNewUrl = false;
                callbacksByUrl.add(callback);
            }
            if (url.contains("/assets/"))
                Log.e("InvokeFileDownload", url + " Add " + isNewUrl);
            return isNewUrl;
        }
    }

    public boolean addFinishCallback(String url, FinishDownloadFileCallback callback) {
        boolean isNewUrl = true;
        synchronized (finishLock) {
            List<FinishDownloadFileCallback> callbacksByUrl = downloadFileCallbacks.get(url);
            if (callbacksByUrl == null) {
                callbacksByUrl = new ArrayList<>();
                callbacksByUrl.add(callback);
                downloadFileCallbacks.put(
                        url,
                        callbacksByUrl
                );
            } else {
                isNewUrl = false;
                callbacksByUrl.add(callback);
            }
            return isNewUrl;
        }
    }

    public void invokeFinishCallbacks(String url, DownloadFileState state) {
        List<FinishDownloadFileCallback> callbacks = new ArrayList<>();
        synchronized (finishLock) {
            List<FinishDownloadFileCallback> localCallbacks = downloadFileCallbacks.get(url);
            if (localCallbacks != null) {
                callbacks.addAll(localCallbacks);
            }
            downloadFileCallbacks.remove(url);
        }
        for (FinishDownloadFileCallback callback : callbacks) {
            callback.finish(state);
        }
    }
    public FilesDownloadManager(Context context, int cacheSize) {
        cachesHolder = new LruCachesHolder(context, cacheSize);
        File file = new File(
                context.getFilesDir() +
                        File.separator +
                        "ias" +
                        File.separator +
                        "vod",
                "vod_journal.bin"
        );
        vodCacheJournal = new VODCacheJournal(file);
        downloadThreadsHolder = new DownloadThreadsHolder();
    }

    public void useFastDownloader(Runnable runnable) {
        downloadThreadsHolder.useFastCacheDownloader(runnable);
    }

    public void useBundleDownloader(Runnable runnable) {
        downloadThreadsHolder.useBundleDownloader(runnable);
    }

    public void useLocalFilesThread(Runnable runnable) {
        downloadThreadsHolder.useLocalFilesThread(runnable);
    }
}
