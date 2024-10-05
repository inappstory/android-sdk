package com.inappstory.sdk.externalapi.stackfeed;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASStackFeed;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public class IASStackFeedExternalAPIImpl implements IASStackFeed {
    public void get(
            final String feed,
            final String uniqueStackId,
            final AppearanceManager appearanceManager,
            final List<String> tags,
            final IStackFeedResult stackFeedResult
    ) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.stackFeedAPI().get(feed, uniqueStackId, appearanceManager, tags, stackFeedResult);
            }
        });

    }
}
