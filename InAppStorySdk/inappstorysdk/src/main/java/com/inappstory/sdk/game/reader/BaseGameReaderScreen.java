package com.inappstory.sdk.game.reader;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.share.IShareCompleteListener;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

public interface BaseGameReaderScreen {
    void forceFinish();

    void pause();

    void resume();

    void setShowGoodsCallback(ShowGoodsCallback callback);

    void permissionResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    );

    FragmentManager getGameReaderFragmentManager();
}