package com.inappstory.sdk.games.ui.reader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public class GameActivity extends AppCompatActivity implements BaseGameScreen {
    @Override
    public void forceFinish() {

    }

    @Override
    public void close() {

    }

    @Override
    public void pauseScreen() {

    }

    @Override
    public void resumeScreen() {

    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public FragmentManager getScreenFragmentManager() {
        return null;
    }
}
