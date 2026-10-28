package com.inappstory.sdk.inappmessage.ui.widgets;

import android.util.Pair;

public interface IAMContainerCallback {
    void countSafeArea(Pair<Integer, Integer> safeArea);

    void onShown();

    void onClosed();
}
