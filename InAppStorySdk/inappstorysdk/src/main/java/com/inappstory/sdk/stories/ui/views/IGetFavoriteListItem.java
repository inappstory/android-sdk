package com.inappstory.sdk.stories.ui.views;

import android.view.View;

import java.util.List;

import com.inappstory.sdk.stories.ui.list.FavoriteImage;

/**
 * <p>This interface uses for favorite item full customization from stories list
 */

public interface IGetFavoriteListItem {

    /**
     * Use to initialize or inflate favorite cell view, that will be used in list
     * Can be set from bindFavoriteItem method (see SDK documentation for usage example and more information)
     *
     * @param favoriteImages (favoriteImages) contains list of covers for favorite stories in {@link FavoriteImage} type.
     * @param count          (count) contains a size of favorite images list
     */
    View getFavoriteItem(List<FavoriteImage> favoriteImages, int count);

    /**
     * @param favCell        (favCell) is a RelativeLayout, which contains the View returned by getFavoriteItem method. If you need to access the internal View directly - you must firstly set an id for it or access it as  favCell.getChildAt(0)
     * @param favoriteImages (favoriteImages) contains list of covers for favorite stories in {@link FavoriteImage} type.
     * @param count          (count) contains a size of favorite images list
     */
    void bindFavoriteItem(View favCell, List<FavoriteImage> favoriteImages, int count);
}
