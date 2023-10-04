package com.inappstory.sdk.stories.uidomain.list.items.story;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.filedownloader.usecases.StoryPreviewDownload;

import java.util.HashMap;

public class StoryListItemManager implements IStoryListItemManager{
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

    public HashMap<String, String> fileLinks = new HashMap<>();

    public String getFileLink(String link) {
        return fileLinks.get(link);
    }

    public void addLink(String link, String fileLink) {
        fileLinks.put(link, fileLink);
    }
}
