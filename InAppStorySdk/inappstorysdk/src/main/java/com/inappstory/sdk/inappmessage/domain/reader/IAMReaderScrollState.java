package com.inappstory.sdk.inappmessage.domain.reader;


public class IAMReaderScrollState {
    public boolean verticalGestureEnabled() {
        return verticalGestureEnabled;
    }

    public IAMReaderScrollState verticalGestureEnabled(boolean verticalGestureEnabled) {
        this.verticalGestureEnabled = verticalGestureEnabled;
        return this;
    }

    @Override
    public String toString() {
        return "IAMReaderScrollState{" +
                "verticalGestureEnabled=" + verticalGestureEnabled +
                ", currentOffsetY=" + currentOffsetY +
                '}';
    }

    private boolean verticalGestureEnabled = true;

    public boolean passOverscroll() {
        return currentOffsetY <= 0;
    }

    public boolean passSwipeUpCallback() {
        return passSwipeUpCallback;
    }

    public IAMReaderScrollState(float currentOffsetY, boolean passSwipeUpCallback) {
        this.currentOffsetY = currentOffsetY;
        this.passSwipeUpCallback = passSwipeUpCallback;
    }

    private final float currentOffsetY;
    private final boolean passSwipeUpCallback;

    public IAMReaderScrollState copy() {
        return new IAMReaderScrollState(currentOffsetY, passSwipeUpCallback)
                .verticalGestureEnabled(verticalGestureEnabled);
    }
}
