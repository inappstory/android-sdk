package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class StoryReaderPager extends ViewPager {
    public StoryReaderPager(@NonNull Context context) {
        super(context);
    }

    public StoryReaderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
