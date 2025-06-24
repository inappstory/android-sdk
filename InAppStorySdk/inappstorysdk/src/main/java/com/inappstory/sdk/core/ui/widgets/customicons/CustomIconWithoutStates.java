package com.inappstory.sdk.core.ui.widgets.customicons;

import android.view.MotionEvent;
import android.view.View;

import com.inappstory.sdk.ICustomIcon;


public abstract class CustomIconWithoutStates implements ICustomIcon {
    @Override
    public void updateState(View iconView, boolean active, boolean enabled) {

    }

    @Override
    public void touchEvent(View iconView, MotionEvent event) {

    }

    @Override
    public void clickEvent(View iconView) {

    }
}
