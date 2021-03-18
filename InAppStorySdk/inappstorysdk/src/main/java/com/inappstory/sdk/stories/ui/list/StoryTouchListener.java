package com.inappstory.sdk.stories.ui.list;

import android.view.View;

import androidx.annotation.NonNull;

public interface StoryTouchListener {
    void touchDown(View view, int position);

    void touchUp(View view, int position);
}