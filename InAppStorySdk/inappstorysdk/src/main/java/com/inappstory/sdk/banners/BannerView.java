package com.inappstory.sdk.banners;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;

public class BannerView extends FrameLayout {
    public BannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.cs_banner_item, this);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
}
