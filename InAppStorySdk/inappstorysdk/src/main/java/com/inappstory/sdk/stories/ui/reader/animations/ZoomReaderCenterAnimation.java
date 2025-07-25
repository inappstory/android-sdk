package com.inappstory.sdk.stories.ui.reader.animations;

import android.view.View;

public class ZoomReaderCenterAnimation extends ReaderAnimation {

    float startedBackgroundAlpha;

    public ZoomReaderCenterAnimation(
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
        backgroundView.setPivotX(pivotX);
        backgroundView.setPivotY(pivotY);
        backgroundView.setScaleX(value);
        backgroundView.setScaleY(value);
        backgroundView.setAlpha(value);
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setPivotX(pivotX);
        backgroundView.setPivotY(pivotY);
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

