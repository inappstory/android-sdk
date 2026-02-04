package com.inappstory.sdk.externalapi.inappmessage;

import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;

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

    CancellationToken show(
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions,
            InAppMessageViewController controller
    );

    void callback(
            InAppMessageLoadCallback callback
    );
}