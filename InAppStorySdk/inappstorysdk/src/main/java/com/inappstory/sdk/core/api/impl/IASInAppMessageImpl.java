package com.inappstory.sdk.core.api.impl;

import static com.inappstory.sdk.InAppStoryManager.IAS_ERROR_TAG;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.LaunchIAMScreenStrategy;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;


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
                    IAS_ERROR_TAG,
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
            InAppMessageOpenSettings openData,
            FragmentManager fragmentManager,
            int containerId,
            final InAppMessageScreenActions screenActions
    ) {
        IASDataSettingsHolder settingsHolder = (IASDataSettingsHolder) core.settingsAPI();
        if (settingsHolder.anonymous()) {
            InAppStoryManager.showELog(
                    IAS_ERROR_TAG,
                    "In-app messages are unavailable for anonymous mode"
            );
            if (screenActions != null)
                screenActions.readerOpenError("In-app messages are unavailable for anonymous mode");
            return;
        }
        core.screensManager().openScreen(
                null,
                new LaunchIAMScreenStrategy(core)
                        .parentContainer(fragmentManager, containerId)
                        .inAppMessageOpenSettings(openData)
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
