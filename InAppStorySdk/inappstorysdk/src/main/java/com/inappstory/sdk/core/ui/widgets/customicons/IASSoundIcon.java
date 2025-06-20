package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public class IASSoundIcon extends IASDefaultIcon {
    public IASSoundIcon(@NonNull Context context) {
        super(context);
    }

    int getImageIconId(CustomIconState state) {
        switch (state) {
            case ENABLE_INACTIVE:
            case DISABLE_INACTIVE:
                return R.drawable.ic_sound_off;
            default:
                return R.drawable.ic_sound_on;
        }
    }
}