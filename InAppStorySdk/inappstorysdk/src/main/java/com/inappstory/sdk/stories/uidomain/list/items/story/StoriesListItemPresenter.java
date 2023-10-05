package com.inappstory.sdk.stories.uidomain.list.items.story;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;

import java.util.HashMap;

public final class StoriesListItemPresenter implements IStoriesListItemPresenter {
    @Override
    public void getFilePathFromLink(final String url, final IFileDownloadCallback callback) {
        String fileLink = getFileLink(url);
        if (fileLink != null) {
            callback.onSuccess(fileLink);
        } else {
            new StoryPreviewDownload(url, new IFileDownloadCallback() {
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
            }).downloadOrGetFromCache();
        }
    }

    private final HashMap<String, String> fileLinks = new HashMap<>();

    private String getFileLink(String link) {
        return fileLinks.get(link);
    }

    private void addLink(String link, String fileLink) {
        fileLinks.put(link, fileLink);
    }
}
