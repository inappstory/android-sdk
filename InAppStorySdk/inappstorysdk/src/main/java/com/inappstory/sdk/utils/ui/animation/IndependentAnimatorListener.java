package com.inappstory.sdk.utils.ui.animation;

public interface IndependentAnimatorListener {
    void onStart();
    void onUpdate(float progress);
    void onEnd();
}
