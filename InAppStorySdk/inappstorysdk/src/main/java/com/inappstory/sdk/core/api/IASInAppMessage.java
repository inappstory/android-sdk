package com.inappstory.sdk.core.api;

import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.CancellationTokenWithStatus;
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
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    );

    void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions
    );

    void callback(
            InAppMessageLoadCallback callback
    );
}
