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


}
