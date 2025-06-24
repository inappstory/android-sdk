package com.inappstory.sdk.core.ui.widgets.customicons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.R;

public class IASDefaultIcon extends FrameLayout {
    public IASDefaultIcon(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IASDefaultIcon setIconId(int iconId) {
        image.setImageDrawable(getResources().getDrawable(iconId));
        return this;
    }

    AppCompatImageView image;

    private void init(Context context) {
        inflate(context, R.layout.cs_custom_icon, this);
        image = findViewById(R.id.image);
        image.setEnabled(true);
        image.setActivated(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateState(boolean active, boolean enabled) {
        image.setEnabled(enabled);
        image.setActivated(active);
    }
}