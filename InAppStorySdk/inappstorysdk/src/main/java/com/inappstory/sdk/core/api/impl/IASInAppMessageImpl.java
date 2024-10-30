package com.inappstory.sdk.core.api.impl;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASInAppMessage;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageScreenActions;

import java.util.List;

public class IASInAppMessageImpl implements IASInAppMessage {

    private final IASCore core;

    public IASInAppMessageImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public void preload(List<String> inAppMessageIds) {
        core.contentPreload()
    }

    @Override
    public void show(
            String inAppMessageId,
            boolean showOnlyIfLoaded,
            FragmentManager fragmentManager,
            int containerId,
            InAppMessageScreenActions screenActions
    ) {

    }

    @Override
    public void callback(InAppMessageLoadCallback callback) {
        core.callbacksAPI().setCallback(
                IASCallbackType.IN_APP_MESSAGE_LOAD,
                callback
        );
    }
}
