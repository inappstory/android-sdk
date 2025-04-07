package com.inappstory.sdk.game.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.iasutilsconnector.lottie.ILottieView;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.utils.format.StringsUtils;

import java.io.File;

public class LottieLoader implements IGameReaderLoaderView {
    private ILottieView lottieView;
    private float currentProgress = 0f;


    public LottieLoader(Context context, @NonNull File source) {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            lottieView = manager.iasCore().externalUtilsAPI().getUtilsAPI()
                    .getLottieViewGenerator().getView(context);
        if (lottieView != null)
            lottieView.setSource(new Pair<>(
                    StringsUtils.md5(source.getAbsolutePath()),
                    source
            ));
    }

    @Override
    public void launchFinalAnimation() {
        if (lottieView.isLooped()) return;
        setProgressInternal(1f);
    }

    private void setProgressInternal(float newProgress) {
        float oldProgress;
        oldProgress = currentProgress;
        if (newProgress <= oldProgress) return;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator =
                ValueAnimator.ofFloat(oldProgress, newProgress).setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //       synchronized (progressLock) {
                currentProgress = (float) animation.getAnimatedValue();
                lottieView.setAnimProgress(currentProgress);
                //        }
            }
        });
        valueAnimator.start();
    }

    ValueAnimator valueAnimator;

    private boolean launchedLoopedAnimation = false;

    @Override
    public void setProgress(int progress, int max) {
        if (lottieView != null && lottieView.isLooped()) {
            if (!launchedLoopedAnimation) {
                launchedLoopedAnimation = true;
                lottieView.play();
            }
            return;
        }
        float newProgress = 0.9f * progress / max;
        setProgressInternal(newProgress);
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {

    }

    @Override
    public View getView(Context context) {
        return (View) lottieView;
    }
}
