package com.inappstory.sdk.game.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;

import java.io.File;


public class GameProgressLoader extends RelativeLayout implements IGameProgressLoader {

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
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Log.e("ProgressVisibility", visibility + "");
    }

    private boolean canUseLottieAnimation = false;

    IGameProgressLoader progressLoader = null;

    public void launchLoaderAnimation(final File customFile) {
        post(new Runnable() {
            @Override
            public void run() {
                if (progressLoader != null) return;
                IGameReaderLoaderView gameReaderLoaderView = AppearanceManager.getCommonInstance().csGameReaderLoaderView();
                View v;
                if (gameReaderLoaderView != null) {
                    progressLoader = gameReaderLoaderView;
                    v = gameReaderLoaderView.getView(getContext());
                } else if (canUseLottieAnimation && customFile != null && customFile.exists()) {
                    progressLoader = new LottieLoader(getContext(), customFile);
                    v = ((LottieLoader) progressLoader).getView(getContext());
                } else {
                    GameReaderLoadProgressBarWithText loadProgressBar = new GameReaderLoadProgressBarWithText(getContext());
                    progressLoader = loadProgressBar;
                    v = loadProgressBar;
                }
                addView(v);
            }
        });

    }

    @Override
    public void launchFinalAnimation() {
        post(new Runnable() {
            @Override
            public void run() {
                if (progressLoader == null) return;
                Log.e("SetProgress", 100 + " " + 100);
                progressLoader.launchFinalAnimation();
            }
        });
    }

    @Override
    public void setProgress(final int progress, final int max) {
        post(new Runnable() {
            @Override
            public void run() {
                Log.e("SetProgress", progress + " " + max);
                if (progressLoader == null) return;
                progressLoader.setProgress(progress, max);
            }
        });
    }

    @Override
    public void setIndeterminate(final boolean indeterminate) {
        post(new Runnable() {
            @Override
            public void run() {
                if (progressLoader == null) return;
                progressLoader.setIndeterminate(indeterminate);
            }
        });

    }
}
