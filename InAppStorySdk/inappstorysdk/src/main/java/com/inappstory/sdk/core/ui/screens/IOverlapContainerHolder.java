package com.inappstory.sdk.core.ui.screens;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;

public interface IOverlapContainerHolder {
    void openOverlapContainer(
            IOverlapContainerData data,
            FragmentManager fragmentManager,
            OverlapFragmentObserver observer
    );
}
