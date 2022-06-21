package com.inappstory.sdk.stories.ui.list;

import android.view.View;

public interface StoryTouchListener {
    void touchDown(View view, int position);

    void touchUp(View view, int position);
}