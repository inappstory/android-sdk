package com.inappstory.sdk.stories.ui.views;

import android.view.View;

public interface IStoriesListItemWithCustomData<T> extends IStoriesListItem {
    void setCustomData(View itemView, T customData);
}