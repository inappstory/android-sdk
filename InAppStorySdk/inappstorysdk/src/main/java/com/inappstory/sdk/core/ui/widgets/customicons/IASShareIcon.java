package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public class IASShareIcon extends IASDefaultIcon {
    public IASShareIcon(@NonNull Context context) {
        super(context);
    }

    int getImageIconId(CustomIconState state) {
        switch (state) {
            case DISABLE_ACTIVE:
            case DISABLE_INACTIVE:
                return R.drawable.cs_share_disabled;
            default:
                return R.drawable.cs_share;
        }
    }
}