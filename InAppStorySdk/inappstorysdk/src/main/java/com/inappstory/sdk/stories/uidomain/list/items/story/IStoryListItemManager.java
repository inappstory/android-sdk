package com.inappstory.sdk.stories.uidomain.list.items.story;

import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;

public interface IStoryListItemManager {
    void getFilePathFromLink(String url, final IFileDownloadCallback callback);

}
