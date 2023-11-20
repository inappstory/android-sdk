package com.inappstory.sdk.stories.ui.reader;


import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public interface BaseReaderScreen {
    void closeStoryReader(int action);
    void forceFinish();
    void observeGameReader(String observableUID);
    void shareComplete(boolean shared);
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void timerIsLocked();
    void timerIsUnlocked();
    void setShowGoodsCallback(ShowGoodsCallback callback);
    FragmentManager getStoriesReaderFragmentManager();
}
