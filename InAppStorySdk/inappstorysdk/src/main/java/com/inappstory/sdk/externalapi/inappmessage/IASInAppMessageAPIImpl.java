package com.inappstory.sdk.externalapi.inappmessage;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

import java.util.List;

public class IASInAppMessageAPIImpl implements IASInAppMessage {
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
    public void show(
            final InAppMessageOpenSettings inAppMessageOpenSettings,
            final FragmentManager fragmentManager,
            final int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.inAppMessageAPI().show(
                        inAppMessageOpenSettings,
                        fragmentManager,
                        containerId,
                        screenActions
                );
            }
        });
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
