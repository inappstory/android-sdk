package com.inappstory.sdk.stories.ui.reader.animations;

import android.util.Log;
import android.view.View;

public class ZoomReaderAnimation extends ReaderAnimation {

    float startedBackgroundAlpha;
    public ZoomReaderAnimation(
            View backgroundView,
            View foregroundView,
            float pivotX,
            float pivotY
    ) {
        super(backgroundView, foregroundView);
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        Log.e("pivots", pivotX + " " + pivotY);
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
        backgroundView.setAlpha(startedBackgroundAlpha + (1f - startedBackgroundAlpha) * value);

        foregroundView.setPivotX(pivotX);
        foregroundView.setPivotY(pivotY);
        foregroundView.setScaleX(value);
        foregroundView.setScaleY(value);
        foregroundView.setAlpha(value);
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setPivotX(pivotX);
        backgroundView.setPivotY(pivotY);
        backgroundView.setScaleX(value);
        backgroundView.setScaleY(value);
        backgroundView.setAlpha(startedBackgroundAlpha * value);

        foregroundView.setPivotX(pivotX);
        foregroundView.setPivotY(pivotY);
        foregroundView.setScaleX(value);
        foregroundView.setScaleY(value);
        foregroundView.setAlpha(value);
    }

    @Override
    int getAnimationDuration() {
        return 300;
    }
}

