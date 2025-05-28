package com.inappstory.sdk.stories.ui.reader.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class FadeReaderAnimation extends ReaderAnimation {

    float startedBackgroundAlpha;

    public FadeReaderAnimation(View backgroundView) {
        super(backgroundView);
        this.startedBackgroundAlpha = backgroundView.getAlpha();
    }


    @Override
    public ReaderAnimation setAnimations(boolean isStart) {
        if (isStart) this.startedBackgroundAlpha = 0f;
        return super.setAnimations(isStart);
    }

    @Override
    void animatorUpdateStartAnimations(float value) {
        backgroundView.setAlpha(startedBackgroundAlpha + (1f - startedBackgroundAlpha) * value);
      //  foregroundView.setAlpha(value);
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setAlpha(startedBackgroundAlpha * value);
      //  foregroundView.setAlpha(value);
    }

    @Override
    int getStartAnimationDuration() {
        return 400;
    }

    @Override
    int getFinishAnimationDuration() {
        return 200;
    }
}

