package com.inappstory.sdk.stories.outercallbacks.screen;


import android.content.Context;

public interface IOpenStoriesReader {
    void onOpen(
            Context context,
            StoriesReaderAppearanceSettings appearanceSettings,
            StoriesReaderLaunchData launchData
    );
}
