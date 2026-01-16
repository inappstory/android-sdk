package com.inappstory.sdk.externalapi.inappmessage;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

public interface IASInAppMessageExternalAPI {
    void preload(
            InAppMessagePreloadSettings preloadSettings,
            InAppMessageLoadCallback callback
    );

    CancellationToken show(
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );

    void callback(
            InAppMessageLoadCallback callback
    );
}