package com.inappstory.sdk.core.ui.widgets.customicons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public abstract class IASDefaultIcon extends FrameLayout {
    public IASDefaultIcon(@NonNull Context context) {
        super(context);
        init(context);
    }

    AppCompatImageView image;

    private void init(Context context) {
        View.inflate(context, R.layout.cs_custom_icon, null);
        image = findViewById(R.id.image);
        updateState(CustomIconState.ENABLE_ACTIVE);
    }

    abstract int getImageIconId(CustomIconState state);

    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateState(CustomIconState state) {
        image.setImageDrawable(getResources().getDrawable(getImageIconId(state)));
    }
}