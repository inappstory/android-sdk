package com.inappstory.sdk.stories.ui.reader.animations;

public interface HandlerAnimatorListener {
    void onAnimationStart();

    void onAnimationProgress(float progress);

    void onAnimationEnd();
}