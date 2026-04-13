package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Log;

public class IAMReaderScrollState {
    public boolean verticalGestureEnabled() {
        return verticalGestureEnabled;
    }

    public IAMReaderScrollState verticalGestureEnabled(boolean verticalGestureEnabled) {
        this.verticalGestureEnabled = verticalGestureEnabled;
        return this;
    }

    private boolean verticalGestureEnabled = true;

    public boolean passOverscroll() {
        return currentOffsetY <= 0;
    }

    public IAMReaderScrollState(float currentOffsetY) {
        Log.e("passOverscroll", "" + currentOffsetY);
        this.currentOffsetY = currentOffsetY;
    }

    private final float currentOffsetY;

    public IAMReaderScrollState copy() {
        return new IAMReaderScrollState(currentOffsetY)
                .verticalGestureEnabled(verticalGestureEnabled);
    }
}
