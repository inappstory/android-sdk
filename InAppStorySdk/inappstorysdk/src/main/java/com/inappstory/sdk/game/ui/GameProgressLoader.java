package com.inappstory.sdk.game.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;

import java.io.File;


public class GameProgressLoader extends FrameLayout implements IGameProgressLoader {

    public GameProgressLoader(@NonNull Context context) {
        super(context);
        init();
    }

    public GameProgressLoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameProgressLoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                canUseLottieAnimation = service.hasLottieAnimation();
            }
        });
    }

    private boolean canUseLottieAnimation = false;

    IGameProgressLoader progressLoader;

    public void launchLoaderAnimation(File customFile) {
        removeAllViewsInLayout();
        IGameReaderLoaderView gameReaderLoaderView = AppearanceManager.getCommonInstance().csGameReaderLoaderView();
        if (gameReaderLoaderView != null) {
            progressLoader = gameReaderLoaderView;
            addView(gameReaderLoaderView.getView(getContext()));
        } else if (canUseLottieAnimation && customFile != null) {
            progressLoader = new LottieLoader(getContext(), customFile);
            addView(((LottieLoader) progressLoader).getView(getContext()));
        } else {
            GameReaderLoadProgressBarWithText loadProgressBar = new GameReaderLoadProgressBarWithText(getContext());
            progressLoader = loadProgressBar;
            addView(loadProgressBar);
        }
    }

    @Override
    public void launchFinalAnimation() {
        if (progressLoader == null) return;
        progressLoader.launchFinalAnimation();
    }

    @Override
    public void setProgress(int progress, int max) {
        if (progressLoader == null) return;
        progressLoader.setProgress(progress, max);
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        if (progressLoader == null) return;
        progressLoader.setIndeterminate(true);
    }
}
