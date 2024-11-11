package com.inappstory.sdk.stories.outercallbacks.common.objects;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.data.IInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

public interface IOpenInAppMessageReader extends IOpenReader {
    void onOpen(
            IInAppMessage inAppMessage,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );
}
