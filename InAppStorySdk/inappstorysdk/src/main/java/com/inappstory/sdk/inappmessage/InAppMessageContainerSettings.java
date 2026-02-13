package com.inappstory.sdk.inappmessage;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class InAppMessageContainerSettings {
    private int containerId = 0;
    private FragmentManager fragmentManager = null;

    public int containerId() {
        return containerId;
    }

    public FragmentManager fragmentManager() {
        return fragmentManager;
    }

    public FrameLayout layout() {
        return layout;
    }

    private FrameLayout layout = null;


    public InAppMessageContainerSettings fragment(
            @NonNull FragmentManager fragmentManager,
            int containerId
    ) {
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        return this;
    }

    public InAppMessageContainerSettings layout(
            @NonNull FrameLayout layout
    ) {
        this.layout = layout;
        return this;
    }
}
