package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;

import java.util.List;

/**
 * <p>This interface uses for favorite item full customization from stories list
 */

public interface IGetFavoriteListItem {

    /**
     * Use to initialize or inflate favorite cell view, that will be used in list
     **/
    View getFavoriteItem();

    /**
     * @param favCell (favCell) is a RelativeLayout, which contains the View returned by getFavoriteItem method. If you need to access the internal View directly - you must firstly set an id for it or access it as  favCell.getChildAt(0)
     */
    void bindFavoriteItem(View favCell, List<Integer> backgroundColors, int count);

    /**
     * @param favCell        (favCell) is a RelativeLayout, which contains the View returned by getFavoriteItem method.
     *                       If you need to access the internal View directly - you must firstly set an id for it or access it as favCell.getChildAt(0)
     * @param favoriteImages (favoriteImages) contains list of covers for favorite stories in {@link StoryFavoriteImage} type.
     * @param count          (count) contains a size of favorite images list
     */
    void setImages(View favCell, List<String> favoriteImages, List<Integer> backgroundColors, int count);
}
