package com.inappstory.sdk.core.api.impl;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.core.ui.screens.inappmessagereader.LaunchIAMScreenStrategy;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageOpenSettings;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.List;

public class IASInAppMessageImpl implements IASInAppMessage {

    private final IASCore core;

    public IASInAppMessageImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void preload(List<String> inAppMessageIds, InAppMessageLoadCallback callback) {
        core.contentLoader().inAppMessageDownloadManager().clearLocalData();
        core.contentPreload().downloadInAppMessages(inAppMessageIds, callback);
    }

    @Override
    public void show(
            InAppMessageOpenSettings openData,
            FragmentManager fragmentManager,
            int containerId,
            final InAppMessageScreenActions screenActions
    ) {
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
