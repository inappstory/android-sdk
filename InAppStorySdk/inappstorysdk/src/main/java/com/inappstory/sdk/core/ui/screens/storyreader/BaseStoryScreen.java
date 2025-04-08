package com.inappstory.sdk.core.ui.screens.storyreader;


import android.graphics.Point;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.ui.screens.BaseScreen;

public interface BaseStoryScreen extends BaseScreen {
    void closeWithAction(int action);
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void timerIsLocked();
    void timerIsUnlocked();
    void disableDrag(boolean disable);
    void disableSwipeUp(boolean disable);
    void disableClose(boolean disable);
    Point getContainerSize();
    FragmentManager getScreenFragmentManager();
}
