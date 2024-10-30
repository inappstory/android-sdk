package com.inappstory.sdk.core.api;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

import java.util.List;

public interface IASInAppMessage {
    void preload(
            List<String> inAppMessageIds
    );

    void show(
            String inAppMessageId,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );

    void callback(
            InAppMessageLoadCallback callback
    );
}
