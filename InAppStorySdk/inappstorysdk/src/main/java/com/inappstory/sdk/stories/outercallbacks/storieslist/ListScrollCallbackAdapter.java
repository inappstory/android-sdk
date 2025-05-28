package com.inappstory.sdk.stories.outercallbacks.storieslist;

import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;

import java.util.List;

public abstract class ListScrollCallbackAdapter implements ListScrollCallback {

    /*   @Override
       public void userInteractionStart() {

       }

       @Override
       public void onOverscroll(int dx, int dy) {

       }

   @Override
       public void userInteractionEnd() {

       }
   */
    @Override
    public void scrollStart() {

    }


    @Override
    public void onVisibleAreaUpdated(List<ShownStoriesListItem> shownStoriesListItemData) {

    }

    @Override
    public void scrollEnd() {

    }


}
