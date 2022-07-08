package com.inappstory.sdk.stories.ui.views;

import android.view.View;

/**
 * <p>This interface uses for full customization of story list items
 */

public interface IStoriesListItem {

    /**
     * Use to initialize or inflate simple view, that will be used in list items
     */
    View getView();

    /**
     * Use to initialize or inflate view with video cover, that will be used in list items
     * To work with video cells, it is recommended to use a class from the VideoPlayer library as
     * a container for displaying video and the loadVideo(String videoUrl) method to launch.
     * This class provides for caching video covers.
     */
    View getVideoView();

    /**
     * Use to get id for custom list cell (can be useful to do something with current story)
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param id (id) contains a story id
     */
    void setId(View itemView, int id);

    /**
     * Use to set title for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param title (title) contains a title string from story
     * @param titleColor (titleColor) contains a color for title string
     */
    void setTitle(View itemView, String title, Integer titleColor);

    /**
     * Use to set image cover for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param path (path) contains a local path to story's cover image.
     * @param backgroundColor (backgroundColor) contains background color to story's cover.
     */
    void setImage(View itemView, String path, int backgroundColor);

    /**
     * Use to check and set sound status if necessary for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param hasAudio (hasAudio) check if current story has audio content.
     */
    void setHasAudio(View itemView, boolean hasAudio);

    /**
     * Use to set video cover for custom list cell. Use only if getVideoView is set.
     * @param itemView (itemView) contains a view that was initialized in getVideoView() method.
     * @param videoPath (videoPath) contains a local path to story's cover video.
     */
    void setVideo(View itemView, String videoPath);

    /**
     * Use to check and set sound status if necessary for custom list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param isOpened (isOpened) check if story was already opened for current user.
     */
    void setOpened(View itemView, boolean isOpened);


}
