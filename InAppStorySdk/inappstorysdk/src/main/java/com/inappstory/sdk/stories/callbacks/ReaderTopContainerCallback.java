package com.inappstory.sdk.stories.callbacks;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;

public interface ReaderTopContainerCallback {
    @NonNull
    View getView(
            @NonNull HashMap<String, Object> data,
            @NonNull OverlappingContainerActions actions
    );

    void onBackPress(@NonNull OverlappingContainerActions actions);
}
