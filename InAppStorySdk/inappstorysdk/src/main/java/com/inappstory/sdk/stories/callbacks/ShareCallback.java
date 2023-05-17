package com.inappstory.sdk.stories.callbacks;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.HashMap;



public interface ShareCallback {
    void onShareOld(String url, String title, String description, String id);

    @NonNull View shareView(
            @NonNull HashMap<String, Object> shareData,
            @NonNull ShareActions actions
    );

    void onBackPress(@NonNull ShareActions actions);
}