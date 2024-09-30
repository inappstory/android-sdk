package com.inappstory.sdk.stories.callbacks;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.api.IASCallback;

import java.util.HashMap;

public interface ShareCallback extends IASCallback {
    @NonNull
    View getView(
            @NonNull Context context,
            @NonNull HashMap<String, Object> data,
            @NonNull OverlappingContainerActions actions
    );

    void viewIsVisible(View view);

    boolean onBackPress(View view, @NonNull OverlappingContainerActions actions);
}
