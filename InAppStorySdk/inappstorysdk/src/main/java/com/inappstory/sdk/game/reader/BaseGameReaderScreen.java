package com.inappstory.sdk.game.reader;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public interface BaseGameReaderScreen {
    void closeGameReader(int action);
    void forceFinish();
    void shareComplete(String shareId, boolean shared);
    void pause();
    void resume();
    void setShowGoodsCallback(ShowGoodsCallback callback);
    void permissionResult();
    FragmentManager getGameReaderFragmentManager();
}