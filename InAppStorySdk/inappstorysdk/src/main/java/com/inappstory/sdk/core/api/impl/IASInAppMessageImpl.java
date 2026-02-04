package com.inappstory.sdk.core.api.impl;


import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.CancellationTokenWithStatus;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.LaunchIAMScreenStrategy;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;


public class IASInAppMessageImpl implements IASInAppMessage {

    private final IASCore core;

    public IASInAppMessageImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void preload(InAppMessagePreloadSettings preloadSettings, InAppMessageLoadCallback callback) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            callback.loadError();
            return;
        }
        core.contentLoader().inAppMessageDownloadManager().clearLocalData();
        core.contentPreload().downloadInAppMessages(preloadSettings, callback);
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            FragmentManager fragmentManager,
            int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .cancellationToken(cancellationToken)
                        .parentContainer(fragmentManager, containerId)
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageScreenActions(screenActions)
        );
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            FrameLayout frameLayout,
            InAppMessageScreenActions screenActions,
            InAppMessageViewController controller
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .cancellationToken(cancellationToken)
                        .frameLayout(frameLayout)
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageViewController(controller)
                        .inAppMessageScreenActions(screenActions)
        );
    }


    @Override
    public void callback(InAppMessageLoadCallback callback) {
        core.callbacksAPI().setCallback(
                IASCallbackType.IN_APP_MESSAGE_LOAD,
                callback
        );
    }
}
