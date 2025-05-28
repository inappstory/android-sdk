package com.inappstory.sdk.core.api;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

import java.util.List;

public interface IASInAppMessage {
    void preload(
            InAppMessagePreloadSettings preloadSettings,
            InAppMessageLoadCallback callback
    );

    void show(
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );

    void callback(
            InAppMessageLoadCallback callback
    );
}
