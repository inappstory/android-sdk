package io.casestory.sdk.stories.ui.views;

import android.view.View;

public interface IStoriesListItem {
    View getView();
    void setTitle(View itemView, String title, Integer titleColor);
    void setSource(View itemView, String source);
    void setImage(View itemView, String url, int backgroundColor);
    void setOpened(View itemView, boolean isOpened);
}
