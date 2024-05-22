package com.inappstory.sdk.stories.stackfeed;

import android.content.Context;

public interface IStackFeedActions {
    void openReader(Context context);

    void openReader(Context context, boolean showNewStories);

    void unsubscribe();
}
