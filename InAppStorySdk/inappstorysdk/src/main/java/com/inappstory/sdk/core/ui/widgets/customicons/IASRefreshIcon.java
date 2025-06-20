package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public class IASRefreshIcon extends IASDefaultIcon {
    public IASRefreshIcon(@NonNull Context context) {
        super(context);
    }

    int getImageIconId(CustomIconState state) {
        return R.drawable.ic_refresh;
    }
}