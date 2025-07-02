package com.inappstory.sdk.core.ui.widgets.customicons;

import android.view.MotionEvent;
import android.view.View;

import com.inappstory.sdk.ICustomIcon;
import com.inappstory.sdk.ICustomIconState;


public abstract class CustomIconWithoutStates implements ICustomIcon {
    @Override
    public void updateState(View iconView, ICustomIconState iconState) {

    }

    @Override
    public void touchEvent(View iconView, MotionEvent event) {

    }

    @Override
    public void clickEvent(View iconView) {

    }
}
