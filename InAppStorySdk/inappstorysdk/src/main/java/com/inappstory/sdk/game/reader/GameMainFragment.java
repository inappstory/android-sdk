package com.inappstory.sdk.game.reader;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.HashMap;

public class GameMainFragment extends Fragment
        implements BaseGameReaderScreen, OverlapFragmentObserver, BackPressHandler {

    @Override
    public void closeGameReader(int action) {

    }

    @Override
    public void forceFinish() {

    }

    @Override
    public void shareComplete(String shareId, boolean shared) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult() {

    }

    @Override
    public FragmentManager getGameReaderFragmentManager() {
        return null;
    }

    @Override
    public void closeView(HashMap<String, Object> data) {

    }

    @Override
    public void viewIsOpened() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
