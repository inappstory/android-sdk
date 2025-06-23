package com.inappstory.sdk;

import android.view.MotionEvent;
import android.view.View;


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
