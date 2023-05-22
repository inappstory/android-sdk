package com.inappstory.sdk.stories.callbacks;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;

public interface ShareCallback {
    @NonNull
    View getView(
            @NonNull Context context,
            @NonNull HashMap<String, Object> data,
            @NonNull OverlappingContainerActions actions
    );

    void viewIsVisible(View view);

    boolean onBackPress(@NonNull OverlappingContainerActions actions);
}
