package com.inappstory.sdk.core.ui.screens;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public interface BaseScreen {
    void forceFinish();

    void close();

    void pauseScreen();

    void resumeScreen();

    void setShowGoodsCallback(ShowGoodsCallback callback);

    void permissionResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    );

    FragmentManager getScreenFragmentManager();
}
