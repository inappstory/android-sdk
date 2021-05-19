package com.inappstory.sdk.stories.ui.views;

import android.view.View;

public interface IStoriesListItem {
    View getView();
    View getVideoView();
    void setTitle(View itemView, String title, Integer titleColor);
    void setImage(View itemView, String url, int backgroundColor);
    void setHasAudio(View itemView, boolean hasAudio);
    void setHasVideo(View itemView, String videoUrl, String url, int backgroundColor);
    void setOpened(View itemView, boolean isOpened);
}
