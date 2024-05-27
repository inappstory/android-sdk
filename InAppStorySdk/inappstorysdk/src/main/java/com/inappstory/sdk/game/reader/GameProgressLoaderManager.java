package com.inappstory.sdk.game.reader;

import android.view.View;

import com.inappstory.sdk.modulesconnector.utils.lottie.ILottieView;
import com.inappstory.sdk.stories.ui.views.IProgressLoader;

public class GameProgressLoaderManager {
    private ILottieView lottieView;
    private View customLoaderView = null;
    private IProgressLoader loaderView;
    private boolean loaderViewIsOverloaded;

    public void useLottieAnimation() {}

    public GameProgressLoaderManager(
            IProgressLoader loaderView,
            View customLoaderView,
            boolean loaderViewIsOverloaded,
            ILottieView lottieView
    ) {
        this.loaderView = loaderView;
        this.lottieView = lottieView;
        this.loaderViewIsOverloaded = loaderViewIsOverloaded;
        this.customLoaderView = customLoaderView;
    }
}
