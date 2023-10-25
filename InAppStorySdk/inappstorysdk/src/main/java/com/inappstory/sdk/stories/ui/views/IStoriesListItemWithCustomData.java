package com.inappstory.sdk.stories.ui.views;

import android.view.View;

/**
 * <p>This interface uses for full customization of story list items
 */

public interface IStoriesListItemWithCustomData<T> extends IStoriesListItem {
    void setCustomData(View itemView, T customData);
}
