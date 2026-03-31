package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Log;

public class IAMReaderScrollState {
    public boolean passOverscroll() {
        return currentOffsetY <= 0;
    }

    public IAMReaderScrollState(float currentOffsetY) {
        Log.e("passOverscroll", "" + currentOffsetY);
        this.currentOffsetY = currentOffsetY;
    }

    private final float currentOffsetY;
}
