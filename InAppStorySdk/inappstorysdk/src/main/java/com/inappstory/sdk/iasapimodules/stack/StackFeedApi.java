package com.inappstory.sdk.iasapimodules.stack;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.iasapimodules.NotImplementedYetException;
import com.inappstory.sdk.iasapimodules.settings.ISettingsProviderApi;
import com.inappstory.sdk.stories.stackfeed.IStackFeedResult;

import java.util.List;

public class StackFeedApi implements IStackFeedApi {

    public StackFeedApi(ISettingsProviderApi settingsProviderApi) {
        this.settingsProviderApi = settingsProviderApi;
    }

    private final ISettingsProviderApi settingsProviderApi;

    @Override
    public void getStackFeed(
            String feed,
            String uniqueStackId,
            List<String> tags,
            AppearanceManager appearanceManager,
            IStackFeedResult stackFeedResult
    ) {
        throw new NotImplementedYetException();
    }
}
