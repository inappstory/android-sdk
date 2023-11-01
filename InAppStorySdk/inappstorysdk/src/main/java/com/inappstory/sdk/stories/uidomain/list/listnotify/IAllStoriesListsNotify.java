package com.inappstory.sdk.stories.uidomain.list.listnotify;

import com.inappstory.sdk.stories.ui.list.adapters.IStoriesListAdapter;


public interface IAllStoriesListsNotify {
    void unsubscribe();

    void subscribe();

    void bindListAdapter(IStoriesListAdapter storiesListAdapter);

    void changeUserId();

    void clearAllFavorites();
}
