package com.inappstory.sdk.iasapimodules.single;

import android.content.Context;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.iasapimodules.NotImplementedYetException;
import com.inappstory.sdk.iasapimodules.settings.ISettingsProviderApi;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.callbacks.IShowStoryOnceCallback;

public class SingleStoryApi implements ISingleStoryApi {
    public SingleStoryApi(ISettingsProviderApi settingsProviderApi) {
        this.settingsProviderApi = settingsProviderApi;
    }

    private final ISettingsProviderApi settingsProviderApi;

    @Override
    public void showStory(
            String storyId,
            Context context,
            AppearanceManager manager
    ) {
        throw new NotImplementedYetException();
    }

    @Override
    public void showStory(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryCallback callback
    ) {
        throw new NotImplementedYetException();
    }

    @Override
    public void showStory(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryCallback callback,
            Integer slide
    ) {
        throw new NotImplementedYetException();
    }

    @Override
    public void showStoryOnce(
            String storyId,
            Context context,
            AppearanceManager manager,
            IShowStoryOnceCallback callback
    ) {
        throw new NotImplementedYetException();
    }
}
