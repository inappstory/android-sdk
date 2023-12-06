package com.inappstory.sdk.stories.ui.oldreader.animations;

public class DisabledReaderAnimation extends ReaderAnimation {
    public DisabledReaderAnimation() {
        super();
    }

    @Override
    void animatorUpdateStartAnimations(float value) {

    }

    @Override
    void animatorUpdateFinishAnimations(float value) {

    }


    @Override
    int getFinishAnimationDuration() {
        return 0;
    }

    @Override
    int getStartAnimationDuration() {
        return 0;
    }
}

