package com.inappstory.sdk.ugc.list;

import android.view.View;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

import java.util.List;

/**
 * <p>This interface uses for favorite item full customization from stories list
 */

public interface IStoriesListUGCItem {

    /**
     * Use to initialize or inflate ugc cell view, that will be used in list
     **/
    View getView();

    /**
     * Use to set image cover for custom ugc list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param path (path) contains a local path to ugc cell's cover image.
     * @param backgroundColor (backgroundColor) contains background color to ugc cell's cover.
     */
    void setImage(View itemView, String path, int backgroundColor);

    /**
     * Use to set title for custom ugc list cell
     * @param itemView (itemView) contains a view that was initialized in getView() method.
     * @param title (title) contains a title string from cell
     * @param titleColor (titleColor) contains a color for title string
     */
    void setTitle(View itemView, String title, Integer titleColor);

}
