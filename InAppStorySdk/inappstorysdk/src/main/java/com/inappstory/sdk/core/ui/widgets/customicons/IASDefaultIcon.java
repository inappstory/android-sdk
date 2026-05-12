package com.inappstory.sdk.core.ui.widgets.customicons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.inappstory.sdk.ICustomIconState;
import com.inappstory.sdk.R;

public class IASDefaultIcon extends FrameLayout {
    public IASDefaultIcon(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IASDefaultIcon setIconId(int iconId) {
        Context context = getContext();
        if (context != null) {
            image.setImageDrawable(
                    ResourcesCompat.getDrawable(
                            context.getResources(),
                            iconId,
                            context.getTheme()
                    )
            );
        }
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
    public void updateState(ICustomIconState iconState) {
        image.setEnabled(iconState.enabled());
        image.setActivated(iconState.active());
    }
}