package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public class IASLikeIcon extends IASDefaultIcon {
    public IASLikeIcon(@NonNull Context context) {
        super(context);
    }

    int getImageIconId(CustomIconState state) {
        switch (state) {
            case DISABLE_ACTIVE:
                return R.drawable.cs_thumb_up_disabled;
            case ENABLE_INACTIVE:
                return R.drawable.cs_thumb_up_outline;
            case DISABLE_INACTIVE:
                return R.drawable.cs_thumb_up_outline_disabled;
            default:
                return R.drawable.cs_thumb_up;
        }
    }
}