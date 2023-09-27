package com.inappstory.sdk.stories.ui.reader.animations;

import android.view.View;

public class PopupReaderAnimation extends ReaderAnimation {
    public PopupReaderAnimation(
            View backgroundView,
            View foregroundView,
            float yFrom,
            float yTo
    ) {
        super(backgroundView, foregroundView);
        this.yFrom = yFrom;
        this.yTo = yTo;
    }

    private float yFrom;
    private float yTo;

    @Override
    void animatorUpdateStartAnimations(float value) {
        backgroundView.setTranslationY((1f - value) * (yFrom - yTo));
        foregroundView.setTranslationY((1f - value) * (yFrom - yTo));
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setTranslationY((1f - value) * (yTo - yFrom));
        foregroundView.setTranslationY((1f - value) * (yTo - yFrom));
    }

    @Override
    int getAnimationDuration() {
        return 300;
    }
}

