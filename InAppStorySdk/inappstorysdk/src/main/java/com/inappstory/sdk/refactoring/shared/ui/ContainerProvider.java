package com.inappstory.sdk.refactoring.shared.ui;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class ContainerProvider {
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


    public ContainerProvider fragment(
            @NonNull FragmentManager fragmentManager,
            int containerId
    ) {
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        return this;
    }

    public ContainerProvider layout(
            @NonNull FrameLayout layout
    ) {
        this.layout = layout;
        return this;
    }
}
