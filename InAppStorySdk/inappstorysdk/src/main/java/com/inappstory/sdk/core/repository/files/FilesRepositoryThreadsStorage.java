package com.inappstory.sdk.core.repository.files;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FilesRepositoryThreadsStorage {
    //Lazy threads initialization
    ExecutorService getFontDownloadThread() {
        synchronized (threadCreationLock) {
            if (fontDownloadThread == null)
                fontDownloadThread = Executors.newFixedThreadPool(1);
            return fontDownloadThread;
        }
    }

    ExecutorService getGoodsWidgetPreviewDownloadThread() {
        synchronized (threadCreationLock) {
            if (goodsWidgetPreviewDownloadThread == null)
                goodsWidgetPreviewDownloadThread = Executors.newFixedThreadPool(1);
            return goodsWidgetPreviewDownloadThread;
        }
    }

    ExecutorService getHomeScreenWidgetPreviewDownloadThread() {
        synchronized (threadCreationLock) {
            if (homeScreenWidgetPreviewDownloadThread == null)
                homeScreenWidgetPreviewDownloadThread = Executors.newFixedThreadPool(1);
            return homeScreenWidgetPreviewDownloadThread;
        }
    }

    ExecutorService getStoryPreviewDownloadThread() {
        synchronized (threadCreationLock) {
            if (storyPreviewDownloadThread == null)
                storyPreviewDownloadThread = Executors.newFixedThreadPool(1);
            return storyPreviewDownloadThread;
        }
    }

    ExecutorService getGameSplashDownloadThread() {
        synchronized (threadCreationLock) {
            if (gameSplashDownloadThread == null)
                gameSplashDownloadThread = Executors.newFixedThreadPool(1);
            return gameSplashDownloadThread;
        }
    }

    private final Object threadCreationLock = new Object();

    private ExecutorService fontDownloadThread;
    private ExecutorService goodsWidgetPreviewDownloadThread;
    private ExecutorService homeScreenWidgetPreviewDownloadThread;
    private ExecutorService storyPreviewDownloadThread;
    private ExecutorService gameSplashDownloadThread;
}
