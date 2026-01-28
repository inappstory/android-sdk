package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

public interface IOpenInAppMessageReader extends IOpenReader {
    void onOpenInFragment(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );

    void onOpenInFrameLayout(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
            FrameLayout layout,
            InAppMessageScreenActions screenActions
    );
}
