package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;


public interface IOpenInAppMessageReader extends IOpenReader {
    void onOpenInFragment(
            FragmentManager fragmentManager,
            int containerId
    );

    void onOpenInLayout(
            FrameLayout frameLayout
    );
}