package com.inappstory.sdk.stories.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class CachedImageView extends ImageView {
    public CachedImageView(Context context) {
        super(context);
    }

    public CachedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CachedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public CachedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
