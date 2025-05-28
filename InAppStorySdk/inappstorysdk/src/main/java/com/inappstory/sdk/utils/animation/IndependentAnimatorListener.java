package com.inappstory.sdk.utils.animation;

public interface IndependentAnimatorListener {
    void onStart();
    void onUpdate(float progress);
    void onEnd();
}
