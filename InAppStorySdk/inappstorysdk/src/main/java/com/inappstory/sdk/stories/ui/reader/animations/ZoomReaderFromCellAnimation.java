package com.inappstory.sdk.stories.ui.reader.animations;

import android.view.View;

public class ZoomReaderFromCellAnimation extends ReaderAnimation {

    float startedBackgroundAlpha;

    public ZoomReaderFromCellAnimation(
            View backgroundView,
            float pivotX,
            float pivotY
    ) {
        super(backgroundView);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.startedBackgroundAlpha = backgroundView.getAlpha();
    }

    private float pivotX;
    private float pivotY;


    @Override
    void animatorUpdateStartAnimations(float value) {
        backgroundView.setTranslationX(pivotX * (1 - value));
        backgroundView.setTranslationY(pivotY * (1 - value));
        backgroundView.setScaleX(value);
        backgroundView.setScaleY(value);
        backgroundView.setAlpha(value);
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setTranslationX(pivotX * (1 - value));
        backgroundView.setTranslationY(pivotY * (1 - value));
        backgroundView.setScaleX(value);
        backgroundView.setScaleY(value);
        backgroundView.setAlpha(startedBackgroundAlpha * value);
    }

    @Override
    int getStartAnimationDuration() {
        return 300;
    }

    @Override
    int getFinishAnimationDuration() {
        return 300;
    }
}

