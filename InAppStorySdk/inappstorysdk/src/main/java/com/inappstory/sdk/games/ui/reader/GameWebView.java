package com.inappstory.sdk.games.ui.reader;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.ui.views.IASWebView;

public class GameWebView extends IASWebView {
    public GameWebView(@NonNull Context context) {
        super(context);
    }

    public GameWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
