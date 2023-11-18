package com.inappstory.sdk.stories.ui.reader;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

public interface BaseReaderScreen {
    void closeStoryReader(int action);
    void forceFinish();
    void observeGameReader(String observableUID);
    void shareComplete(boolean shared);
    void removeStoryFromFavorite(int id);
    void removeAllStoriesFromFavorite();
    void timerIsLocked();
    void timerIsUnlocked();
    Context getReaderContext();
    FragmentManager getStoriesReaderFragmentManager();
}
