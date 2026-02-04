package com.inappstory.sdk.externalapi.inappmessage;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.CancellationToken;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.CancellationTokenImpl;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;

public class IASInAppMessageExternalAPIImpl implements IASInAppMessageExternalAPI {
    @Override
    public void preload(
            final InAppMessagePreloadSettings inAppMessagePreloadSettings,
            final InAppMessageLoadCallback callback
    ) {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppMessageAPI().preload(
                        inAppMessagePreloadSettings, callback
                );
            }
        });
    }

    @Override
    public CancellationToken show(
            final InAppMessageOpenSettings inAppMessageOpenSettings,
            final FragmentManager fragmentManager,
            final int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        final CancellationTokenWithStatus token =
                new CancellationTokenImpl("External IAM data: " +
                        inAppMessageOpenSettings.toString()
                );
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.cancellationTokenPool().addToken(token);
                core.inAppMessageAPI().show(
                        token,
                        inAppMessageOpenSettings,
                        fragmentManager,
                        containerId,
                        screenActions
                );
            }
        });
        return token;
    }

    @Override
    public CancellationToken show(
            InAppMessageOpenSettings inAppMessageOpenSettings,
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions,
            InAppMessageViewController controller
    ) {
        final CancellationTokenWithStatus token =
                new CancellationTokenImpl("External IAM data: " +
                        inAppMessageOpenSettings.toString()
                );
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.cancellationTokenPool().addToken(token);
                core.inAppMessageAPI().show(
                        token,
                        inAppMessageOpenSettings,
                        frameLayout,
                        screenActions,
                        controller
                );
            }
        });
        return token;
    }


    @Override
    public void callback(final InAppMessageLoadCallback callback) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppMessageAPI().callback(
                        callback
                );
            }
        });
    }
}
