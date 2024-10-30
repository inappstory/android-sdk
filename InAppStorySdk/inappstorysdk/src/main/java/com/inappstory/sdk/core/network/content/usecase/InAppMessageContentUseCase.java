package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.dataholders.models.IInAppMessage;
import com.inappstory.sdk.core.dataholders.models.IResource;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;

import java.util.List;

public class InAppMessageContentUseCase {
    private final IASCore core;
    private final IInAppMessage message;

    public InAppMessageContentUseCase(IASCore core, IInAppMessage message) {
        this.core = core;
        this.message = message;
    }

    public void load(InAppMessageLoadCallback loadCallback) {
        int slidesCount = message.actualSlidesCount();
        for (int i = 0; i < slidesCount; i++) {

            List<IResource> slideStaticResources = message.staticResources(i);
        }
    }
}
