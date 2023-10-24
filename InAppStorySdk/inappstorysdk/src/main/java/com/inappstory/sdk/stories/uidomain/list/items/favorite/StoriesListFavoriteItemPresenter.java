package com.inappstory.sdk.stories.uidomain.list.items.favorite;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.IFilesDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class StoriesListFavoriteItemPresenter implements IStoriesListFavoriteItemPresenter {
    private void getFilePathFromLink(
            final String url,
            final IFileDownloadCallback callback
    ) {
        if (url == null) {
            callback.onError(-1, "");
            return;
        }
        String fileLink = getFileLink(url);
        if (fileLink != null) {
            callback.onSuccess(fileLink);
        } else {
            IASCoreManager.getInstance().filesRepository.getStoryPreview(
                    url,
                    new IFileDownloadCallback() {
                @Override
                public void onSuccess(final String fileAbsolutePath) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            addLink(url, fileAbsolutePath);
                            callback.onSuccess(fileAbsolutePath);
                        }
                    });
                }

                @Override
                public void onError(int errorCode, String error) {
                    callback.onError(errorCode, error);
                }
            });
        }
    }

    private final Object fileLinksLock = new Object();
    private final HashMap<String, String> fileLinks = new HashMap<>();
    private final ArrayList<String> fileLinkProcessed = new ArrayList<>();

    private String getFileLink(String link) {
        synchronized (fileLinksLock) {
            return fileLinks.get(link);
        }
    }

    private void addLink(String link, String fileLink) {
        synchronized (fileLinksLock) {
            fileLinks.put(link, fileLink);
        }
    }

    private void addProcessedLink(String link) {
        synchronized (fileLinksLock) {
            fileLinkProcessed.add(link);
        }
    }

    private void clearProcessedLinks() {
        synchronized (fileLinksLock) {
            fileLinkProcessed.clear();
        }
    }

    private int getProcessedLinkSize() {
        synchronized (fileLinksLock) {
            return fileLinkProcessed.size();
        }
    }

    @Override
    public void getFilePathsFromLinks(final List<String> urls, final IFilesDownloadCallback callback) {
        clearProcessedLinks();
        final int size = urls.size();
        for (final String url : urls) {
            getFilePathFromLink(url, new IFileDownloadCallback() {
                @Override
                public void onSuccess(String fileAbsolutePath) {
                    addLink(url, fileAbsolutePath);
                    addProcessedLink(url);
                    if (getProcessedLinkSize() == size) {
                        prepareLinksAndSendToCallback(urls, callback);
                    }
                }

                @Override
                public void onError(int errorCode, String error) {
                    addProcessedLink(url);
                    if (getProcessedLinkSize() == size) {
                        prepareLinksAndSendToCallback(urls, callback);
                    }
                }
            });
        }
    }

    private void prepareLinksAndSendToCallback(List<String> urls, IFilesDownloadCallback callback) {
        List<String> resultUrls = new ArrayList<>();
        for (String url : urls) {
            resultUrls.add(getFileLink(url));
        }
        callback.onSuccess(resultUrls);
    }
}
