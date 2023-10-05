package com.inappstory.sdk.stories.uidomain.list.items.favorite;

import com.inappstory.sdk.stories.filedownloader.IFilesDownloadCallback;

import java.util.List;

public interface IStoriesListFavoriteItemPresenter {
    void getFilePathsFromLinks(final List<String> urls, IFilesDownloadCallback callback);
}
