package com.inappstory.sdk.core.ui.screens.storyreader;


import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.ui.screens.BaseScreen;

public interface BaseStoryScreen extends BaseScreen {
    void closeWithAction(int action);
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void timerIsLocked();
    void timerIsUnlocked();
    void disableDrag(boolean disable);
    FragmentManager getScreenFragmentManager();
}
