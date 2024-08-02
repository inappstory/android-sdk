package com.inappstory.sdk.core.ui.screens.storyreader;


import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public interface BaseStoryReaderScreen {
    void closeStoryReader(int action);
    void forceFinish();
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void timerIsLocked();
    void timerIsUnlocked();
    void pauseReader();
    void resumeReader();
    void disableDrag(boolean disable);
    void setShowGoodsCallback(ShowGoodsCallback callback);
    FragmentManager getStoriesReaderFragmentManager();
}
