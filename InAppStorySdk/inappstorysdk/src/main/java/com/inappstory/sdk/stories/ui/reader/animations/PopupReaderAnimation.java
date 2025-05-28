package com.inappstory.sdk.stories.ui.reader.animations;

import android.view.View;

public class PopupReaderAnimation extends ReaderAnimation {
    public PopupReaderAnimation(
            View backgroundView,
            float yFrom,
            float yTo
    ) {
        super(backgroundView);
        this.yFrom = yFrom;
        this.yTo = yTo;
    }

    private float yFrom;
    private float yTo;

    @Override
    void animatorUpdateStartAnimations(float value) {
        backgroundView.setAlpha(value);
        backgroundView.setTranslationY((1f - value) * (yFrom - yTo));
      //  foregroundView.setTranslationY((1f - value) * (yFrom - yTo));
    }

    @Override
    void animatorUpdateFinishAnimations(float value) {
        backgroundView.setAlpha(value);
        backgroundView.setTranslationY((1f - value) * (yTo - yFrom));
      //  foregroundView.setTranslationY((1f - value) * (yTo - yFrom));
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

