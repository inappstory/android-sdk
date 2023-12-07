package com.inappstory.sdk.stories.ui.widgets;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;


public class RoundedOutlineProvider extends ViewOutlineProvider {
    private float radius;

    public RoundedOutlineProvider(float radius) {
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(
                0,
                0,
                view.getWidth(),
                view.getHeight(),
                radius
        );
    }
}
