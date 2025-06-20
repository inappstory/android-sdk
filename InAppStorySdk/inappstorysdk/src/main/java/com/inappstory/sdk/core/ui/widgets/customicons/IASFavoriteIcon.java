package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;
import androidx.annotation.NonNull;

import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.R;

public class IASFavoriteIcon extends IASDefaultIcon {
    public IASFavoriteIcon(@NonNull Context context) {
        super(context);
    }

    int getImageIconId(CustomIconState state) {
        switch (state) {
            case DISABLE_ACTIVE:
                return R.drawable.cs_bookmark_disabled;
            case ENABLE_INACTIVE:
                return R.drawable.cs_bookmark_outline;
            case DISABLE_INACTIVE:
                return R.drawable.cs_bookmark_outline_disabled;
            default:
                return R.drawable.cs_bookmark;
        }
    }
}