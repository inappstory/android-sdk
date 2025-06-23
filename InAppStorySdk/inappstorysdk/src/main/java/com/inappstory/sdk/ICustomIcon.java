package com.inappstory.sdk;

import android.content.Context;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;

public interface ICustomIcon {
    View createIconView(Context context, SizeF maxSizeInPx);

    void updateState(View iconView, boolean active, boolean enabled);

    void touchEvent(View iconView, MotionEvent event);

    void clickEvent(View iconView);
}
