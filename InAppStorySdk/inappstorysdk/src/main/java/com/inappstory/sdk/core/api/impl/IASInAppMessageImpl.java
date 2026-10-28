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
import com.inappstory.sdk.inappmessage.InAppMessageContainerProvider;
import com.inappstory.sdk.inappmessage.InAppMessageContainerSettings;
import com.inappstory.sdk.inappmessage.InAppMessageData;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessagePreloadSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;
import com.inappstory.sdk.inappmessage.InAppMessageViewController;
import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;


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
                        .containerProvider(new InAppMessageContainerProvider() {
                            @Override
                            public InAppMessageContainerSettings provideContainer(InAppMessageData messageData) {
                                return new InAppMessageContainerSettings().fragment(fragmentManager, containerId);
                            }

                            @Override
                            public IAMViewController layoutController() {
                                return null;
                            }
                        })
                        .inAppMessageOpenSettings(openData)
                        .inAppMessageScreenActions(screenActions)
        );
    }

    @Override
    public void show(
            CancellationTokenWithStatus cancellationToken,
            InAppMessageOpenSettings openData,
            InAppMessageContainerProvider containerProvider,
            InAppMessageScreenActions screenActions
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
                        .containerProvider(containerProvider)
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
                        .layout(frameLayout)
                        .containerProvider(new InAppMessageContainerProvider() {
                            @Override
                            public InAppMessageContainerSettings provideContainer(InAppMessageData messageData) {
                                return new InAppMessageContainerSettings().layout(frameLayout);
                            }

                            @Override
                            public IAMViewController layoutController() {
                                return controller;
                            }
                        })
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
